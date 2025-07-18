package org.db_poultry.pojo.SupplyPOJO;

public class SupplyType {
    private final int supplyTypeId;
    private final String name;
    private final String unit;
    private final String imagePath;

    public SupplyType(int supplyTypeId, String name, String unit, String imagePath) {
        this.supplyTypeId = supplyTypeId;
        this.name = name;
        this.unit = unit;
        this.imagePath = imagePath;
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

    public String getImagePath() { return imagePath; }
}
