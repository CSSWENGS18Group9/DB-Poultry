package org.db_poultry.pojo.SupplyPOJO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class SupplyRecord {
    private final int supplyRecordId;
    private final int supplyTypeId;
    private final BigDecimal currentQuantity;
    private final BigDecimal deleted;
    private final BigDecimal added;
    private final String unit;
    private final Date date;
    private final boolean retrieved;
    private final BigDecimal price;

    public SupplyRecord(int supplyRecordId, int supplyTypeId, BigDecimal currentQuantity, BigDecimal deleted, BigDecimal added, String unit, Date date, boolean retrieved, BigDecimal price) {
        this.supplyRecordId = supplyRecordId;
        this.supplyTypeId = supplyTypeId;
        this.currentQuantity = scale(currentQuantity);
        this.deleted = scale(deleted);
        this.added = scale(added);
        this.unit = unit;
        this.date = date;
        this.retrieved = retrieved;
        this.price = price;
    }

    private static BigDecimal scale(BigDecimal value) {
        return value == null ? null : value.setScale(4, RoundingMode.DOWN);
    }

    public int getSupplyRecordId() {
        return supplyRecordId;
    }

    public int getSupplyTypeId() {
        return supplyTypeId;
    }

    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    public BigDecimal getDeleted() {
        return deleted;
    }

    public BigDecimal getAdded() {
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

    public BigDecimal getPrice() {
        return price;
    }

}
