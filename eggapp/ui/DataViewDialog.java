package com.example.eggapp.ui;

import com.example.eggapp.model.InventoryItem;
import com.example.eggapp.utils.DataExporter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

public class DataViewDialog extends JDialog {
    private JTable dataTable;
    private JButton exportButton;

    public DataViewDialog(JFrame parent, List<InventoryItem> items) {
        super(parent, "数据查看", true);
        initUI(items);
    }

    private void initUI(List<InventoryItem> items) {
        setSize(600, 400);
        setLocationRelativeTo(getParent());

        // 表格
        String[] columnNames = {"ID", "名称", "数量", "安全库存", "补货点", "鸡蛋类型ID", "成本价", "包装类型"};
        Object[][] data = new Object[items.size()][columnNames.length];
        for (int i = 0; i < items.size(); i++) {
            InventoryItem item = items.get(i);
            data[i] = new Object[]{
                    item.getId(),
                    item.getName(),
                    item.getQuantity(),
                    item.getSafetyStock(),
                    item.getReorderPoint(),
                    item.getEggTypeId(),
                    item.getCostPrice(),
                    item.getPackagingType()
            };
        }
        dataTable = new JTable(data, columnNames);
        add(new JScrollPane(dataTable), BorderLayout.CENTER);

        // 导出按钮
        exportButton = new JButton("导出为CSV");
        exportButton.addActionListener(this::exportAction);
        add(exportButton, BorderLayout.SOUTH);
    }

    private void exportAction(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                DataExporter.exportToCsv(getTableData(), filePath);
                JOptionPane.showMessageDialog(this, "导出成功！");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "导出失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private List<InventoryItem> getTableData() {
        // 从表格中获取数据并转换为 InventoryItem 列表
        // 这里需要根据实际需求实现
        return List.of();
    }
}
