package com.example.eggapp.model;

import java.util.Date;

/**
 * 订单交易类，用于表示一次订单交易。
 * 包含交易类型、库存项ID、交易数量、总价等信息。
 */
public class OrderTransaction {
    private int id; // 交易ID
    private OrderType type; // 交易类型（采购/销售）
    private int inventoryId; // 库存项ID
    private int quantity; // 交易数量
    private double totalPrice; // 交易总价
    private Date transactionDate; // 交易日期

    // 构造函数
    public OrderTransaction(OrderType type, int inventoryId, int quantity, double totalPrice, Date transactionDate) {
        this.type = type;
        this.inventoryId = inventoryId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.transactionDate = transactionDate;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "OrderTransaction{" +
                "id=" + id +
                ", type=" + type +
                ", inventoryId=" + inventoryId +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                ", transactionDate=" + transactionDate +
                '}';
    }
}
