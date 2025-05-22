package org.db_poultry.db.flock.pojo;

import java.sql.Timestamp;

/**
 * this is a pojo for the Flock Details table
 */
public class FlockDetails {
    private final int flockId;
    private final Timestamp startingDate;
    private final Timestamp fdDate;
    private final int currentCount;
    private final int depletedCount;

    /**
     * constructor for the flock details
     * @param flockId the flock id
     * @param startingDate the starting date
     * @param fdDate the date of the flock details
     * @param currentCount the current count of the flock
     * @param depletedCount the depleted count of the flock (number of chickens dead)
     */
    public FlockDetails(int flockId, Timestamp startingDate, Timestamp fdDate, int currentCount, int depletedCount) {
        this.flockId = flockId;
        this.startingDate = startingDate;
        this.fdDate = fdDate;
        this.currentCount = currentCount;
        this.depletedCount = depletedCount;
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

    /**
     * getter for the flock details date
     * @return flock details date
     */
    public Timestamp getFdDate() {
        return fdDate;
    }

    /**
     * getter for the current (chicken) count
     * @return current count
     */
    public int getCurrentCount() {
        return currentCount;
    }


    /**
     * getter for the depleted (dead chicken) count
     * @return depleted count
     */
    public int getDepletedCount() {
        return depletedCount;
    }
}
