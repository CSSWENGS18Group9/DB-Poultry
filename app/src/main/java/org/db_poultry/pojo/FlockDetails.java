package org.db_poultry.pojo;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * this is a pojo for the Flock Details table
 */
public class FlockDetails {
    private final int flockDetailsId;
    private final int flockId;
    private final Date fdDate;
    private final int depletedCount;

    /**
     * constructor for the flock details
     *
     * @param flockDetailsId the flock details id
     * @param flockId        the flock id
     * @param fdDate         the date of the flock details
     * @param depletedCount  the depleted count of the flock (number of chickens dead)
     */
    public FlockDetails(int flockDetailsId, int flockId, Date fdDate, int depletedCount) {
        this.flockDetailsId = flockDetailsId;
        this.flockId = flockId;
        this.fdDate = fdDate;
        this.depletedCount = depletedCount;
    }

    /**
     * getter for the flock details id
     *
     * @return starting date
     */
    public int getFlockDetailsId() {
        return flockDetailsId;
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
     * getter for the flock details date
     *
     * @return flock details date
     */
    public Date getFdDate() {
        return fdDate;
    }

    /**
     * getter for the depleted (dead chicken) count
     *
     * @return depleted count
     */
    public int getDepletedCount() {
        return depletedCount;
    }
}
