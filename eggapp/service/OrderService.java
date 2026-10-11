package com.example.eggapp.service;

import com.example.eggapp.dao.DatabaseHelper;
import com.example.eggapp.model.Order;
import com.example.eggapp.model.OrderTransaction;
import com.example.eggapp.model.PurchaseOrder;
import com.example.eggapp.model.SalesOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private DatabaseHelper databaseHelper;

    @Transactional
    public void processOrderTransaction(OrderTransaction transaction) throws SQLException {
        // 更新库存
        String updateSql = "UPDATE inventory SET quantity = quantity - ? WHERE id = ?";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setInt(1, transaction.getQuantity());
            pstmt.setInt(2, transaction.getInventoryId());
            pstmt.executeUpdate();
        }

        // 创建订单记录
        String orderSql = "INSERT INTO orders (type, inventory_id, quantity, total_price) VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(orderSql)) {
            pstmt.setString(1, transaction.getType().name());
            pstmt.setInt(2, transaction.getInventoryId());
            pstmt.setInt(3, transaction.getQuantity());
            pstmt.setDouble(4, transaction.getTotalPrice());
            pstmt.executeUpdate();
        }
    }

    /**
     * 保存一条通用订单（供 Swing 界面调用）
     */
    public void saveOrder(Order order) throws SQLException {
        DatabaseHelper.saveOrder(order);
    }

    /**
     * 处理采购订单；默认实现由 {@link OrderServiceImpl} 覆盖。
     */
    public void processPurchaseOrder(PurchaseOrder order) throws OrderProcessingException {
        throw new UnsupportedOperationException("采购订单处理未实现");
    }

    /**
     * 处理销售订单；默认实现由 {@link OrderServiceImpl} 覆盖。
     */
    public void processSalesOrder(SalesOrder order) throws OrderProcessingException {
        throw new UnsupportedOperationException("销售订单处理未实现");
    }

    /**
     * 查询最近 N 天的采购订单。
     */
    public List<PurchaseOrder> getRecentPurchases(int days) throws SQLException {
        throw new UnsupportedOperationException("未实现");
    }

    /**
     * 查询最近 N 天的销售订单。
     */
    public List<SalesOrder> getRecentSales(int days) throws SQLException {
        throw new UnsupportedOperationException("未实现");
    }
}
