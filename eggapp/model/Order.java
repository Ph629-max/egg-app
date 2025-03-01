package com.example.eggapp.model;

import java.util.Date;

public class Order {
    private int id;
    private int shopId;
    private int inventoryId;
    private int quantity;
    private double price;
    private double costPrice;
    private String orderType;
    private Date orderDate;
    private OrderStatus status;
    private boolean paid;
    private String paymentMethod;
    private int packagingTypeId;
    private String packagingType;

    // 全参构造函数
    public Order(int shopId, int inventoryId, int quantity, double price, double costPrice,
                 String orderType, Date orderDate, OrderStatus status, boolean paid,
                 String paymentMethod, int packagingTypeId, String packagingType) {
        this.shopId = shopId;
        this.inventoryId = inventoryId;
        this.quantity = quantity;
        this.price = price;
        this.costPrice = costPrice;
        this.orderType = orderType;
        this.orderDate = orderDate;
        this.status = status;
        this.paid = paid;
        this.paymentMethod = paymentMethod;
        this.packagingTypeId = packagingTypeId;
        this.packagingType = packagingType;
    }

    // Getter & Setter 方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getShopId() { return shopId; }
    public void setShopId(int shopId) { this.shopId = shopId; }
    public int getInventoryId() { return inventoryId; }
    public void setInventoryId(int inventoryId) { this.inventoryId = inventoryId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getCostPrice() { return costPrice; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public int getPackagingTypeId() { return packagingTypeId; }
    public void setPackagingTypeId(int packagingTypeId) { this.packagingTypeId = packagingTypeId; }
    public String getPackagingType() { return packagingType; }
    public void setPackagingType(String packagingType) { this.packagingType = packagingType; }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", shopId=" + shopId +
                ", inventoryId=" + inventoryId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", costPrice=" + costPrice +
                ", orderType='" + orderType + '\'' +
                ", orderDate=" + orderDate +
                ", status=" + status +
                ", paid=" + paid +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", packagingTypeId=" + packagingTypeId +
                ", packagingType='" + packagingType + '\'' +
                '}';
    }
}
