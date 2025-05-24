package org.db_poultry.db.flock.pojo;

import java.sql.Timestamp;

/**
 * this is a pojo for the Flock table
 */
public class Flock {
    private final int flockId;
    private final Timestamp startingDate;

    /**
     * constructor for the flock
     * @param flockId the id of the flock
     * @param startingDate the starting date of the flock
     */
    public Flock(int flockId, Timestamp startingDate) {
        this.flockId = flockId;
        this.startingDate = startingDate;
    }

    /**
     * getter for the flock id
     * @return flock id
     */
    public int getFlockId() {
        return flockId;
    }

    /**
     * getter for the starting date
     * @return starting date
     */
    public Timestamp getStartingDate() {
        return startingDate;
    }
}
