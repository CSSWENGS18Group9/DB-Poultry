package org.db_poultry.pojo.FlockPOJO;

import java.util.ArrayList;
import java.util.List;

/**
 * The "complete table/entity" for the Flock uses Flock (SQL Table/POJO) and
 * FlockDetails (SQL Table/POJO) as its constituents
 */
public class FlockComplete {
    private final Flock flock;
    private final List<FlockDetails> flock_details;

    /**
     * constructor
     *
     * @param flock the flock pojo (or the parent table)
     */
    public FlockComplete(Flock flock) {
        this.flock = flock;

        // We will add the flock_details, to add use addFlockDetail()
        this.flock_details = new ArrayList<>();
    }

    /**
     * getter for the flock
     *
     * @return flock POJO
     */
    public Flock getFlock() {
        return flock;
    }

    /**
     * getter for the flock details (as a list) that has the flock as their "parent"
     *
     * @return a list of flock detail POJOs
     */
    public List<FlockDetails> getFlockDetails() {
        return flock_details;
    }

    /**
     * adds a flock detail to the POJO
     *
     * @param fd the flock detail we want to add
     */
    public void addFlockDetail(FlockDetails fd) {
        // yes, flock_details is FINAL, but we can add to it
        this.flock_details.add(fd);
    }
}
