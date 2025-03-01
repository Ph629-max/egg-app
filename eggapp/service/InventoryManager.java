package com.example.eggapp.service;

import com.example.eggapp.model.InventoryItem;
import com.example.eggapp.model.SortingRule;
import com.example.eggapp.dao.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class InventoryManager {
    private Map<String, SortingRule> sortingRules = new HashMap<>();
    private List<InventoryItem> rawInventory = new ArrayList<>();
    private List<InventoryItem> processedInventory = new ArrayList<>();

    // 加载分拣规则
    public void loadSortingRules() throws SQLException {
        String sql = "SELECT egg_type_id, packaging_type, damage_ratio, packing_cost FROM sorting_rules";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                SortingRule rule = new SortingRule(
                        rs.getInt("egg_type_id"),
                        rs.getString("packaging_type"),
                        rs.getDouble("damage_ratio"),
                        rs.getDouble("packing_cost")
                );
                sortingRules.put(rule.getPackagingType(), rule);
            }
        }
    }

    // 应用分拣规则生成加工库存
    public void applySortingRules() {
        processedInventory = new ArrayList<>();
        for (InventoryItem item : rawInventory) {
            for (SortingRule rule : sortingRules.values()) {
                if (item.getEggTypeId() == rule.getEggTypeId()) {
                    processItem(item, rule);
                }
            }
        }
    }

    // 处理单个库存项
    private void processItem(InventoryItem item, SortingRule rule) {
        int quantityAfterSorting = (int) (item.getQuantity() * rule.getRatio());
        int damagedQuantity = (int) (quantityAfterSorting * rule.getDamageRatio());
        int finalQuantity = quantityAfterSorting - damagedQuantity;
        if (finalQuantity <= 0) return;

        double finalCost = (item.getCostPrice() * quantityAfterSorting) / finalQuantity + rule.getPackingCost();

        InventoryItem processedItem = new InventoryItem(
                item.getId(),
                item.getName() + " - " + rule.getPackagingType(),
                finalQuantity,
                item.getSafetyStock(),
                item.getReorderPoint(),
                item.getEggTypeId(),
                finalCost,
                rule.getPackagingType()
        );
        processedInventory.add(processedItem);
    }

    // 获取低库存项
    public List<InventoryItem> getLowStockItems() {
        List<InventoryItem> lowStockItems = new ArrayList<>();
        for (InventoryItem item : rawInventory) {
            if (item.isBelowSafetyStock()) {
                lowStockItems.add(item);
            }
        }
        return lowStockItems;
    }

    // Getter & Setter
    public List<InventoryItem> getRawInventory() { return rawInventory; }
    public void setRawInventory(List<InventoryItem> rawInventory) { this.rawInventory = rawInventory; }
    public List<InventoryItem> getProcessedInventory() { return processedInventory; }
}
