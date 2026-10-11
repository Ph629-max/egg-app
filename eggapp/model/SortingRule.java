package com.example.eggapp.model;

/**
 * 分拣规则：描述某种蛋品在指定包装方式下的损耗率与包装成本。
 */
public class SortingRule {
    private int id;
    private int eggTypeId;
    private String packagingType;
    private double damageRatio;
    private double packingCost;
    private boolean giftPackaging;
    private double extraCost;

    /** 无参构造：供 Jackson 反序列化 / 持久化使用。 */
    public SortingRule() {
    }

    public SortingRule(int eggTypeId, String packagingType, double damageRatio, double packingCost) {
        this.eggTypeId = eggTypeId;
        this.packagingType = packagingType;
        this.damageRatio = damageRatio;
        this.packingCost = packingCost;
    }

    public SortingRule(int eggTypeId, String packagingType, double damageRatio, double packingCost,
                       boolean giftPackaging, double extraCost) {
        this(eggTypeId, packagingType, damageRatio, packingCost);
        this.giftPackaging = giftPackaging;
        this.extraCost = extraCost;
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
    public boolean isGiftPackaging() { return giftPackaging; }
    public void setGiftPackaging(boolean giftPackaging) { this.giftPackaging = giftPackaging; }
    public double getExtraCost() { return extraCost; }
    public void setExtraCost(double extraCost) { this.extraCost = extraCost; }

    // 计算分拣后的比例：1 - 损耗率
    public double getRatio() {
        return 1 - damageRatio;
    }

    @Override
    public String toString() {
        return String.format("SortingRule{eggTypeId=%d, packagingType='%s', damageRatio=%.3f, packingCost=%.2f}",
                eggTypeId, packagingType, damageRatio, packingCost);
    }
}
