package org.db_poultry.pojo.SupplyPOJO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;

public class SupplyComplete {
    final int supply_id;
    final int supply_type_id;
    final Date date;
    final String supply_name;
    final String unit;
    final BigDecimal added;
    final BigDecimal consumed;
    final BigDecimal current;
    final boolean retrieved;

    public SupplyComplete(int supply_id, int supply_type_id, Date date, String supply_name, String unit, BigDecimal added, BigDecimal consumed, BigDecimal current, boolean retrieved) {
        this.supply_id = supply_id;
        this.supply_type_id = supply_type_id;
        this.date = date;
        this.supply_name = supply_name;
        this.unit = unit;
        this.added = (added == null) ? null : added.setScale(4, RoundingMode.DOWN);
        this.consumed = (consumed == null) ? null : consumed.setScale(4, RoundingMode.DOWN);
        this.current = (current == null) ? null : current.setScale(4, RoundingMode.DOWN);
        this.retrieved = retrieved;
    }

    public int getSupply_id() {
        return supply_id;
    }

    public int getSupply_type_id() {
        return supply_type_id;
    }

    public Date getDate() {
        return date;
    }

    public String getSupply_name() {
        return supply_name;
    }

    public String getUnit() {
        return unit;
    }

    public BigDecimal getAdded() {
        return added;
    }

    public BigDecimal getConsumed() {
        return consumed;
    }

    public BigDecimal getCurrent() { return current; }

    public boolean isRetrieved() {
        return retrieved;
    }
}
