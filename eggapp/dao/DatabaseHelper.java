package com.example.eggapp.dao;

import com.example.eggapp.model.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseHelper.class);
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource dataSource;

    static {
        config.setJdbcUrl("jdbc:mysql://localhost:3306/inventory_db");
        config.setUsername("root");
        config.setPassword("1234");
        config.setMaximumPoolSize(10); // 设置连接池大小
        config.setMinimumIdle(2);     // 设置最小空闲连接数
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * 获取所有分拣规则
     */
    public static List<SortingRule> getAllSortingRules() throws SQLException {
        List<SortingRule> rules = new ArrayList<>();
        String sql = "SELECT * FROM sorting_rules";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rules.add(mapToSortingRule(rs));
            }
        } catch (SQLException e) {
            logger.error("Failed to load sorting rules from database", e);
            throw e;
        }
        return rules;
    }

    /**
     * 保存分拣规则
     */
    public static void saveSortingRule(SortingRule rule) throws SQLException {
        String sql = "INSERT INTO sorting_rules (egg_type_id, packaging_type, damage_ratio, packing_cost) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rule.getEggTypeId());
            pstmt.setString(2, rule.getPackagingType());
            pstmt.setDouble(3, rule.getDamageRatio());
            pstmt.setDouble(4, rule.getPackingCost());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to save sorting rule: {}", rule, e);
            throw e;
        }
    }

    /**
     * 删除分拣规则
     */
    public static void deleteSortingRule(int ruleId) throws SQLException {
        String sql = "DELETE FROM sorting_rules WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ruleId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to delete sorting rule with ID: {}", ruleId, e);
            throw e;
        }
    }

    /**
     * 将 ResultSet 映射为 SortingRule 对象
     */
    private static SortingRule mapToSortingRule(ResultSet rs) throws SQLException {
        return new SortingRule(
                rs.getInt("egg_type_id"),
                rs.getString("packaging_type"),
                rs.getDouble("damage_ratio"),
                rs.getDouble("packing_cost")
        );
    }
}
