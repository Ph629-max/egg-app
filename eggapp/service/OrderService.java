package com.example.eggapp.service;

import com.example.eggapp.dao.DatabaseHelper;
import com.example.eggapp.model.OrderTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
