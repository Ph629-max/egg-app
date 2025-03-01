package com.example.eggapp.model;

import java.io.Serializable;

/**
 * 库存项实体类，表示一个库存项。
 * 包含库存数量、安全库存、补货点等信息。
 */
public class InventoryItem implements Serializable {
    private int id;
    private String name;
    private int quantity;
    private int safetyStock;
    private int reorderPoint;
    private int eggTypeId;
    private double costPrice;
    private String packagingType;
    private boolean isVirtual;

    // 全参构造函数
    public InventoryItem(int id, String name, int quantity, int safetyStock,
                         int reorderPoint, int eggTypeId, double costPrice,
                         String packagingType, boolean isVirtual) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.safetyStock = safetyStock;
        this.reorderPoint = reorderPoint;
        this.eggTypeId = eggTypeId;
        this.costPrice = costPrice;
        this.packagingType = packagingType;
        this.isVirtual = isVirtual;
    }

    /**
     * 判断是否需要补货
     */
    public boolean needsReplenishment() {
        return quantity <= safetyStock;
    }

    /**
     * 更新库存数量（带验证）
     */
    public void updateQuantity(int delta) throws IllegalArgumentException {
        if (quantity + delta < 0) {
            throw new IllegalArgumentException("库存数量不能为负");
        }
        this.quantity += delta;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getSafetyStock() { return safetyStock; }
    public void setSafetyStock(int safetyStock) { this.safetyStock = safetyStock; }
    public int getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(int reorderPoint) { this.reorderPoint = reorderPoint; }
    public int getEggTypeId() { return eggTypeId; }
    public void setEggTypeId(int eggTypeId) { this.eggTypeId = eggTypeId; }
    public double getCostPrice() { return costPrice; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }
    public String getPackagingType() { return packagingType; }
    public void setPackagingType(String packagingType) { this.packagingType = packagingType; }
    public boolean isVirtual() { return isVirtual; }
    public void setVirtual(boolean virtual) { isVirtual = virtual; }

    @Override
    public String toString() {
        return String.format("%s (ID: %d) - 数量: %d, 成本: ¥%.2f",
                name, id, quantity, costPrice);
    }
}
