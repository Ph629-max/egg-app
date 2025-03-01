package com.example.eggapp.model;

public class SortingRule {
    private int id;
    private int eggTypeId;
    private String packagingType;
    private double damageRatio;
    private double packingCost;

    // 构造函数
    public SortingRule(int eggTypeId, String packagingType, double damageRatio, double packingCost) {
        this.eggTypeId = eggTypeId;
        this.packagingType = packagingType;
        this.damageRatio = damageRatio;
        this.packingCost = packingCost;
    }

    // Getter 和 Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEggTypeId() { return eggTypeId; }
    public void setEggTypeId(int eggTypeId) { this.eggTypeId = eggTypeId; }
    public String getPackagingType() { return packagingType; }
    public void setPackagingType(String packagingType) { this.packagingType = packagingType; }
    public double getDamageRatio() { return damageRatio; }
    public void setDamageRatio(double damageRatio) { this.damageRatio = damageRatio; }
    public double getPackingCost() { return packingCost; }
    public void setPackingCost(double packingCost) { this.packingCost = packingCost; }

    // 计算分拣后的比例
    public double getRatio() {
        return 1 - damageRatio; // 假设 ratio 是 1 - 损耗率
    }
}
