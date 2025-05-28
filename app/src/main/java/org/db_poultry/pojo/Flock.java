package org.db_poultry.pojo;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * this is a pojo for the Flock table
 */
public class Flock {
    private final int flockId;
    private final int startingCount;
    private final Date startingDate;

    /**
     * constructor for the flock
     *
     * @param flockId      the id of the flock
     * @param startingDate the starting date of the flock
     */
    public Flock(int flockId, int startingCount, Date startingDate) {
        this.flockId = flockId;
        this.startingCount = startingCount;
        this.startingDate = startingDate;
    }

    /**
     * getter for the flock id
     *
     * @return flock id
     */
    public int getFlockId() {
        return flockId;
    }

    /**
     * getter for starting count
     *
     * @return starting count
     */
    public int getStartingCount() {
        return startingCount;
    }

    /**
     * getter for the starting date
     *
     * @return starting date
     */
    public Timestamp getStartingDate() {
        return startingDate;
    }
}
