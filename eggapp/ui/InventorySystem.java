package com.example.eggapp.ui;

import com.example.eggapp.dao.DatabaseHelper;
import com.example.eggapp.model.Order;
import com.example.eggapp.model.OrderStatus;
import com.example.eggapp.service.OrderService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InventorySystem extends JFrame {
    private JTable inventoryTable;
    private OrderService orderService;

    public InventorySystem() {
        orderService = new OrderService();
        initUI();
        loadData();
        showWelcomeMessage();
    }

    private void initUI() {
        setTitle("Egg Inventory Management System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 菜单栏
        JMenuBar menuBar = new JMenuBar();
        JMenu inventoryMenu = new JMenu("库存管理");
        JMenuItem addItem = new JMenuItem("添加库存");
        addItem.addActionListener(this::addInventoryAction);
        inventoryMenu.add(addItem);
        menuBar.add(inventoryMenu);

        JMenu orderMenu = new JMenu("订单管理");
        JMenuItem createOrder = new JMenuItem("生成订单");
        createOrder.addActionListener(this::createOrderAction);
        orderMenu.add(createOrder);
        menuBar.add(orderMenu);

        setJMenuBar(menuBar);

        // 表格
        String[] columnNames = {"ID", "名称", "数量", "安全库存", "补货点", "鸡蛋类型ID", "成本价", "包装类型"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        inventoryTable = new JTable(model);
        inventoryTable.setAutoCreateRowSorter(true); // 支持排序
        add(new JScrollPane(inventoryTable), BorderLayout.CENTER);
    }

    private void loadData() {
        // 从数据库加载数据并填充表格
    }

    private void showWelcomeMessage() {
        JOptionPane.showMessageDialog(this,
                "欢迎使用鸡蛋库存管理系统！\n" +
                        "1. 点击“库存管理”添加库存。\n" +
                        "2. 点击“订单管理”生成订单。\n" +
                        "3. 在表格中查看库存和订单数据。",
                "欢迎", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addInventoryAction(ActionEvent e) {
        JTextField nameField = new JTextField();
        JTextField quantityField = new JTextField("1000");
        JTextField safetyStockField = new JTextField("200");
        JTextField reorderPointField = new JTextField("500");
        JTextField eggTypeIdField = new JTextField("1");
        JTextField costPriceField = new JTextField("0.5");
        JTextField packagingTypeField = new JTextField("盒装");

        Object[] fields = {
                "名称:", nameField,
                "数量:", quantityField,
                "安全库存:", safetyStockField,
                "补货点:", reorderPointField,
                "鸡蛋类型ID:", eggTypeIdField,
                "成本价:", costPriceField,
                "包装类型:", packagingTypeField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "添加库存", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                int safetyStock = Integer.parseInt(safetyStockField.getText());
                int reorderPoint = Integer.parseInt(reorderPointField.getText());
                int eggTypeId = Integer.parseInt(eggTypeIdField.getText());
                double costPrice = Double.parseDouble(costPriceField.getText());
                String packagingType = packagingTypeField.getText();

                // 保存到数据库并刷新表格
                JOptionPane.showMessageDialog(this, "库存添加成功！");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "输入格式错误，请检查输入！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private List<String> loadShops() {
        List<String> shops = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, name FROM shops");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                shops.add(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shops;
    }

    private List<String> loadInventoryItems() {
        List<String> items = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, name FROM inventory");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    private void createOrderAction(ActionEvent e) {
        // 加载店铺和库存数据
        List<String> shops = loadShops();
        List<String> inventoryItems = loadInventoryItems();

        // 创建下拉菜单
        JComboBox<String> shopComboBox = new JComboBox<>(shops.toArray(new String[0]));
        JComboBox<String> inventoryComboBox = new JComboBox<>(inventoryItems.toArray(new String[0]));

        JTextField quantityField = new JTextField("100");
        JTextField priceField = new JTextField("1.0");
        JTextField costPriceField = new JTextField();
        JTextField packagingTypeField = new JTextField();
        JTextField orderTypeField = new JTextField("销售");
        JTextField paymentMethodField = new JTextField("现金");

        // 自动填充成本价和包装类型
        inventoryComboBox.addActionListener(event -> {
            String selectedInventory = (String) inventoryComboBox.getSelectedItem();
            if (selectedInventory != null) {
                int inventoryId = Integer.parseInt(selectedInventory.split(" - ")[0]);
                try (Connection conn = DatabaseHelper.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("SELECT cost_price, packaging_type FROM inventory WHERE id = ?")) {
                    stmt.setInt(1, inventoryId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        costPriceField.setText(String.valueOf(rs.getDouble("cost_price")));
                        packagingTypeField.setText(rs.getString("packaging_type"));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Object[] fields = {
                "店铺:", shopComboBox,
                "库存:", inventoryComboBox,
                "数量:", quantityField,
                "价格:", priceField,
                "成本价:", costPriceField,
                "包装类型:", packagingTypeField,
                "订单类型:", orderTypeField,
                "支付方式:", paymentMethodField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "生成订单", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                // 解析选择的店铺和库存
                String selectedShop = (String) shopComboBox.getSelectedItem();
                String selectedInventory = (String) inventoryComboBox.getSelectedItem();
                int shopId = Integer.parseInt(selectedShop.split(" - ")[0]);
                int inventoryId = Integer.parseInt(selectedInventory.split(" - ")[0]);

                int quantity = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());
                double costPrice = Double.parseDouble(costPriceField.getText());
                String orderType = orderTypeField.getText();
                String paymentMethod = paymentMethodField.getText();

                Order order = new Order(shopId, inventoryId, quantity, price, costPrice, orderType, new Date(), OrderStatus.PENDING_PAYMENT, false, paymentMethod, 1, packagingTypeField.getText());
                orderService.saveOrder(order);
                JOptionPane.showMessageDialog(this, "订单生成成功！");
            } catch (NumberFormatException | SQLException ex) {
                JOptionPane.showMessageDialog(this, "输入格式错误或保存失败，请检查输入！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventorySystem().setVisible(true));
    }
}
