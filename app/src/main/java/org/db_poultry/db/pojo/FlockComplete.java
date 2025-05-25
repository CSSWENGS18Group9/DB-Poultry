package org.db_poultry.db.pojo;

import java.util.ArrayList;
import java.util.List;

public class FlockComplete {
    private final Flock flock;
    private final List<FlockDetails> flock_details;

    public FlockComplete(Flock flock) {
        this.flock = flock;
        this.flock_details = new ArrayList<>();
    }

    public Flock getFlock() {
        return flock;
    }

    public List<FlockDetails> getFlockDetails() {
        return flock_details;
    }

    public void addFlockDetail(FlockDetails fd) {
        this.flock_details.add(fd);
    }
}
