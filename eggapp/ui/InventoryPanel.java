package com.example.eggapp.ui;

import com.example.eggapp.model.InventoryItem;
import com.example.eggapp.utils.DataExporter;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    private final InventoryTableModel tableModel = new InventoryTableModel();
    private final JTable inventoryTable = new JTable(tableModel);
    private final JButton refreshBtn = new JButton("刷新");
    private final JButton exportBtn = new JButton("导出CSV");

    public InventoryPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.add(refreshBtn);
        toolBar.add(exportBtn);
        add(toolBar, BorderLayout.NORTH);

        // 表格设置
        inventoryTable.setAutoCreateRowSorter(true);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        add(scrollPane, BorderLayout.CENTER);

        // 按钮事件
        refreshBtn.addActionListener(e -> refreshData());
        exportBtn.addActionListener(e -> exportToCSV());
    }

    public void setInventoryData(List<InventoryItem> items) {
        tableModel.setData(items);
    }

    private void refreshData() {
        // 调用数据库刷新逻辑
        firePropertyChange("refresh", false, true);
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                DataExporter.exportToCsv(tableModel.getData(),
                        fileChooser.getSelectedFile().getPath());
                JOptionPane.showMessageDialog(this, "导出成功");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "导出失败: " + ex.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 自定义表格模型
    private static class InventoryTableModel extends AbstractTableModel {
        private List<InventoryItem> data;
        private final String[] COLUMNS = {"ID", "名称", "数量", "安全库存", "补货点", "成本价", "包装类型"};

        public void setData(List<InventoryItem> newData) {
            this.data = newData;
            fireTableDataChanged();
        }

        public List<InventoryItem> getData() {
            return data;
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return COLUMNS.length; }
        @Override public String getColumnName(int col) { return COLUMNS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            InventoryItem item = data.get(row);
            return switch (col) {
                case 0 -> item.getId();
                case 1 -> item.getName();
                case 2 -> item.getQuantity();
                case 3 -> item.getSafetyStock();
                case 4 -> item.getReorderPoint();
                case 5 -> String.format("¥%.2f", item.getCostPrice());
                case 6 -> item.getPackagingType();
                default -> null;
            };
        }
    }
}
