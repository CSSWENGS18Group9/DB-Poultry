package org.db_poultry.pojo;

public class SupplyType {
    private final int supplyTypeId;
    private final String name;

    public SupplyType(int supplyTypeId, String name) {
        this.supplyTypeId = supplyTypeId;
        this.name = name;
    }

    public int getSupplyTypeId() {
        return supplyTypeId;
    }

    public String getName() {
        return name;
    }
}
