package org.db_poultry.pojo;

import java.util.Date;

public class SupplyRecord {
    private final int supplyRecordId;
    private final int supplyTypeId;
    private final double currentQuantity;
    private final double deleted;
    private final double added;
    private final String unit;
    private final Date date;
    private final boolean retrieved;

    public SupplyRecord(int supplyRecordId, int supplyTypeId, double currentQuantity, double deleted, double added, String unit, Date date, boolean retrieved) {
        this.supplyRecordId = supplyRecordId;
        this.supplyTypeId = supplyTypeId;
        this.currentQuantity = currentQuantity;
        this.deleted = deleted;
        this.added = added;
        this.unit = unit;
        this.date = date;
        this.retrieved = retrieved;
    }

    public int getSupplyRecordId() {
        return supplyRecordId;
    }

    public int getSupplyTypeId() {
        return supplyTypeId;
    }

    public double getCurrentQuantity() {
        return currentQuantity;
    }

    public double getDeleted() {
        return deleted;
    }

    public double getAdded() {
        return added;
    }

    public String getUnit() {
        return unit;
    }

    public Date getDate() {
        return date;
    }

    public boolean isRetrieved() {
        return retrieved;
    }
}
