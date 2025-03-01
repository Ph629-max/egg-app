package com.example.eggapp.service;

import com.example.eggapp.dao.DatabaseHelper;
import com.example.eggapp.model.SortingRule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.sql.SQLException;
import java.util.*;

@Service
public class SortingRuleManager {
    private Map<Integer, List<SortingRule>> ruleMap = new HashMap<>(); // 内存中的规则缓存
    private List<SortingRuleChangeListener> listeners = new ArrayList<>(); // 规则变化监听器

    @Autowired
    private Jedis jedis; // 注入 Redis 客户端

    /**
     * 规则变化监听器接口
     */
    public interface SortingRuleChangeListener {
        void onRulesChanged();
    }

    /**
     * 初始化加载规则
     */
    public void initialize() throws SQLException {
        loadRulesFromDatabase();
    }

    /**
     * 从数据库加载规则，并缓存到 Redis
     */
    public void loadRulesFromDatabase() throws SQLException {
        ruleMap.clear(); // 清空内存缓存
        List<SortingRule> rules = DatabaseHelper.getAllSortingRules(); // 从数据库加载规则
        for (SortingRule rule : rules) {
            ruleMap.computeIfAbsent(rule.getEggTypeId(), k -> new ArrayList<>())
                    .add(rule);
        }
        // 缓存规则到 Redis
        jedis.set("sorting_rules", serializeRules(rules));
        notifyListeners(); // 通知监听器规则已更新
    }

    /**
     * 添加规则变化监听器
     */
    public void addChangeListener(SortingRuleChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * 获取指定类型的规则
     */
    public List<SortingRule> getRulesForType(int eggTypeId) {
        return ruleMap.getOrDefault(eggTypeId, Collections.emptyList());
    }

    /**
     * 添加新规则
     */
    public void addRule(SortingRule newRule) throws SQLException {
        DatabaseHelper.saveSortingRule(newRule); // 保存到数据库
        loadRulesFromDatabase(); // 刷新缓存
    }

    /**
     * 删除规则
     */
    public void deleteRule(int ruleId) throws SQLException {
        DatabaseHelper.deleteSortingRule(ruleId); // 从数据库删除
        loadRulesFromDatabase(); // 刷新缓存
    }

    /**
     * 通知监听器规则变化
     */
    private void notifyListeners() {
        for (SortingRuleChangeListener listener : listeners) {
            listener.onRulesChanged();
        }
    }

    /**
     * 序列化规则列表为 JSON 字符串
     */
    private String serializeRules(List<SortingRule> rules) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(rules);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize rules to JSON", e);
        }
    }

    /**
     * 从 JSON 字符串反序列化为规则列表
     */
    private List<SortingRule> deserializeRules(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, SortingRule.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize rules from JSON", e);
        }
    }

    /**
     * 从 Redis 缓存加载规则
     */
    public void loadRulesFromCache() {
        String cachedRules = jedis.get("sorting_rules");
        if (cachedRules != null) {
            List<SortingRule> rules = deserializeRules(cachedRules);
            ruleMap.clear();
            for (SortingRule rule : rules) {
                ruleMap.computeIfAbsent(rule.getEggTypeId(), k -> new ArrayList<>())
                        .add(rule);
            }
        }
    }
}
