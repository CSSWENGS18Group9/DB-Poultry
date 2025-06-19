package org.db_poultry.pojo.SupplyPOJO;

public class SupplyType {
    private final int supplyTypeId;
    private final String name;
    private final String unit;

    public SupplyType(int supplyTypeId, String name, String unit) {
        this.supplyTypeId = supplyTypeId;
        this.name = name;
        this.unit = unit;
    }

    public int getSupplyTypeId() {
        return supplyTypeId;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }
}
