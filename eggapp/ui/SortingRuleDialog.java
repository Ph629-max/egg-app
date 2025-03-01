package com.example.eggapp.ui;

import com.example.eggapp.model.SortingRule;
import com.example.eggapp.service.SortingRuleManager;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class SortingRuleDialog extends JDialog {
    private final SortingRuleManager ruleManager;
    private JComboBox<Integer> eggTypeCombo = new JComboBox<>(new Integer[]{1,2,3});
    private JTextField packagingField = new JTextField(15);
    private JSpinner damageSpinner = new JSpinner(
            new SpinnerNumberModel(0.1, 0.0, 1.0, 0.01));
    private JSpinner costSpinner = new JSpinner(
            new SpinnerNumberModel(0.5, 0.0, 100.0, 0.1));
    private JCheckBox giftCheck = new JCheckBox("礼盒装");
    private JSpinner extraCostSpinner = new JSpinner(
            new SpinnerNumberModel(2.0, 0.0, 100.0, 0.5));

    public SortingRuleDialog(Frame owner, SortingRuleManager ruleManager) {
        super(owner, "分拣规则管理", true);
        this.ruleManager = ruleManager;
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(7, 2, 5, 5));

        // 表单组件
        add(new JLabel("鸡蛋类型ID:"));
        add(eggTypeCombo);
        add(new JLabel("包装类型:"));
        add(packagingField);
        add(new JLabel("损耗率 (%):"));
        add(damageSpinner);
        add(new JLabel("包装成本:"));
        add(costSpinner);
        add(new JLabel("礼盒装:"));
        add(giftCheck);
        add(new JLabel("附加成本:"));
        add(extraCostSpinner);

        // 操作按钮
        JButton saveBtn = new JButton("保存");
        saveBtn.addActionListener(e -> saveRule());
        add(saveBtn);

        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dispose());
        add(cancelBtn);

        // 对话框设置
        setSize(400, 300);
        setLocationRelativeTo(getOwner());
    }

    private void saveRule() {
        try {
            SortingRule rule = new SortingRule(
                    (Integer) eggTypeCombo.getSelectedItem(),
                    packagingField.getText(),
                    (Double) damageSpinner.getValue(),
                    (Double) costSpinner.getValue(),
                    giftCheck.isSelected(),
                    (Double) extraCostSpinner.getValue()
            );

            ruleManager.addRule(rule);
            JOptionPane.showMessageDialog(this, "规则保存成功");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "保存失败: " + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
