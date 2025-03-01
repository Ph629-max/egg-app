package com.example.eggapp.service;

import com.example.eggapp.dao.DatabaseHelper;
import com.example.eggapp.model.InventoryItem;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AlertService {
    private static final int CHECK_INTERVAL = 3600; // 1小时

    public void startMonitoring() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::checkStockLevels, 0, CHECK_INTERVAL, TimeUnit.SECONDS);
    }

    private void checkStockLevels() {
        try {
            List<InventoryItem> lowStockItems = DatabaseHelper.getLowStockItems();
            if (!lowStockItems.isEmpty()) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null,
                                "低库存预警！需补货商品数: " + lowStockItems.size(),
                                "库存预警",
                                JOptionPane.WARNING_MESSAGE));

                // 记录预警日志
                logAlerts(lowStockItems);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void logAlerts(List<InventoryItem> items) {
        String sql = "INSERT INTO stock_alerts (inventory_id, alert_type, alert_level) VALUES (?, 'LOW_STOCK', 1)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (InventoryItem item : items) {
                pstmt.setInt(1, item.getId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}