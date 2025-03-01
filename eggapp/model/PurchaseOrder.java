package com.example.eggapp.model;

import java.util.Date;

public class PurchaseOrder {
    private int id;
    private int supplierId;
    private int inventoryId;
    private int quantity;
    private double price;
    private Date orderDate;
    private String status;

    // 构造函数
    public PurchaseOrder(int supplierId, int inventoryId, int quantity, double price, Date orderDate, String status) {
        this.supplierId = supplierId;
        this.inventoryId = inventoryId;
        this.quantity = quantity;
        this.price = price;
        this.orderDate = orderDate;
        this.status = status;
    }

    // Getter 和 Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public int getInventoryId() { return inventoryId; }
    public void setInventoryId(int inventoryId) { this.inventoryId = inventoryId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
