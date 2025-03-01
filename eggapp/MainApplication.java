package com.example.eggapp;

import com.example.eggapp.service.*;
import com.example.eggapp.ui.*;

public class MainApplication {
    public static void main(String[] args) {
        try {
            // 初始化服务
            SortingRuleManager ruleManager = new SortingRuleManager();
            ruleManager.initialize();

            InventoryManager inventoryManager = new InventoryManager();
            NotificationService notificationService = new EmailNotificationService();

            OrderService orderService = new OrderServiceImpl(
                    inventoryManager,
                    notificationService
            );

            // 创建主界面
            MainFrame mainFrame = new MainFrame(
                    ruleManager,
                    orderService,
                    inventoryManager
            );

            // 启动界面
            SwingUtilities.invokeLater(() -> {
                mainFrame.setVisible(true);
                mainFrame.loadInitialData();
            });

        } catch (Exception e) {
            System.err.println("系统初始化失败: ");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
