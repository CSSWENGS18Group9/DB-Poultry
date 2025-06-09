package org.db_poultry.pojo;

import java.sql.Date;

public class SupplyComplete {
    final int supply_id;
    final int supply_type_id;
    final Date date;
    final String supply_name;
    final String unit;
    final float added;
    final float consumed;
    final boolean retrieved;

    public SupplyComplete(int supply_id, int supply_type_id, Date date, String supply_name, String unit, float added, float consumed, boolean retrieved) {
        this.supply_id = supply_id;
        this.supply_type_id = supply_type_id;
        this.date = date;
        this.supply_name = supply_name;
        this.unit = unit;
        this.added = added;
        this.consumed = consumed;
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

    public float getAdded() {
        return added;
    }

    public float getConsumed() {
        return consumed;
    }

    public boolean isRetrieved() {
        return retrieved;
    }
}
