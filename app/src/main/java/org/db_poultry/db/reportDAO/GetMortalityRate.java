package org.db_poultry.db.reportDAO;
import org.db_poultry.db.flockDetailsDAO.ReadFlockDetails;
import org.db_poultry.db.flockDAO.ReadFlock;
import org.db_poultry.pojo.FlockPOJO.Flock;

import java.sql.Connection;
import java.sql.Date;

public class GetMortalityRate {

    public static float calculateMortalityRate(Connection conn, Date flockDate) {

        Flock selectedFlock = ReadFlock.getFlockFromADate(conn, flockDate);

        float depleted = ReadFlockDetails.getCumulativeDepletedCount(conn, selectedFlock.getFlockId());
        float startingCount = selectedFlock.getStartingCount();

        return depleted / startingCount;
    }

}
