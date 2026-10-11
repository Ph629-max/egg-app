package com.example.eggapp.ui;

import com.example.eggapp.dao.DatabaseHelper;
import com.example.eggapp.model.InventoryItem;
import com.example.eggapp.service.InventoryManager;
import com.example.eggapp.service.OrderService;
import com.example.eggapp.service.SortingRuleManager;
import com.example.eggapp.utils.LogUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 主窗口：整合库存面板，并提供分拣规则与订单查询入口。
 */
public class MainFrame extends JFrame {

    private final SortingRuleManager ruleManager;
    private final OrderService orderService;
    private final InventoryManager inventoryManager;

    private final InventoryPanel inventoryPanel = new InventoryPanel();
    private final JLabel statusLabel = new JLabel("就绪");

    public MainFrame(SortingRuleManager ruleManager,
                     OrderService orderService,
                     InventoryManager inventoryManager) {
        this.ruleManager = ruleManager;
        this.orderService = orderService;
        this.inventoryManager = inventoryManager;
        initUI();
    }

    private void initUI() {
        setTitle("蛋品库存与订单管理系统");
        setSize(1000, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();

        JMenu inventoryMenu = new JMenu("库存管理");
        JMenuItem refreshItem = new JMenuItem("刷新库存");
        refreshItem.addActionListener(e -> loadInitialData());
        JMenuItem exportItem = new JMenuItem("查看 / 导出");
        exportItem.addActionListener(e -> new DataViewDialog(this, safeInventory()).setVisible(true));
        inventoryMenu.add(refreshItem);
        inventoryMenu.add(exportItem);
        menuBar.add(inventoryMenu);

        JMenu ruleMenu = new JMenu("分拣规则");
        JMenuItem manageRules = new JMenuItem("管理规则...");
        manageRules.addActionListener(e -> new SortingRuleDialog(this, ruleManager).setVisible(true));
        ruleMenu.add(manageRules);
        menuBar.add(ruleMenu);

        JMenu orderMenu = new JMenu("订单管理");
        JMenuItem recentOrders = new JMenuItem("最近 7 天订单");
        recentOrders.addActionListener(e -> showRecentOrders());
        orderMenu.add(recentOrders);
        menuBar.add(orderMenu);

        JMenu helpMenu = new JMenu("帮助");
        JMenuItem about = new JMenuItem("关于");
        about.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "蛋品库存与订单管理系统\n技术栈：Java Swing + MySQL + Redis",
                "关于", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(about);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
        add(inventoryPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        // 分拣规则变化时刷新加工库存
        ruleManager.addChangeListener(() -> SwingUtilities.invokeLater(() -> {
            inventoryManager.applySortingRules();
            statusLabel.setText("分拣规则已更新");
        }));
    }

    /**
     * 加载初始数据：读取库存并应用分拣规则。
     */
    public void loadInitialData() {
        try {
            List<InventoryItem> items = DatabaseHelper.getAllInventoryItems();
            inventoryManager.setRawInventory(items);
            inventoryManager.applySortingRules();
            inventoryPanel.setInventoryData(items);
            statusLabel.setText("已加载库存项 " + items.size() + " 条");
        } catch (Exception ex) {
            LogUtil.error("加载初始数据失败", ex);
            statusLabel.setText("加载数据失败: " + ex.getMessage());
        }
    }

    private List<InventoryItem> safeInventory() {
        List<InventoryItem> items = inventoryManager.getRawInventory();
        return items == null ? new ArrayList<>() : items;
    }

    private void showRecentOrders() {
        try {
            int purchaseCount = orderService.getRecentPurchases(7).size();
            int salesCount = orderService.getRecentSales(7).size();
            JOptionPane.showMessageDialog(this,
                    "最近 7 天：采购订单 " + purchaseCount + " 笔，销售订单 " + salesCount + " 笔",
                    "订单概览", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            LogUtil.error("查询订单失败", ex);
            JOptionPane.showMessageDialog(this, "查询失败: " + ex.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
