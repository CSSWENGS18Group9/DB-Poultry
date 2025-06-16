package org.db_poultry.db.reportDAO;
import org.db_poultry.db.flockDetailsDAO.ReadFlockDetails;
import org.db_poultry.db.flockDAO.ReadFlock;
import org.db_poultry.pojo.FlockPOJO.Flock;
import org.db_poultry.pojo.FlockPOJO.FlockDetails;
import org.db_poultry.pojo.ReportPOJO.MortalityRate;

import java.sql.Connection;
import java.sql.Date;

public class GetMortalityRate {

    public static MortalityRate calculateMortalityRate(Connection conn, Date flockDate) {

        Flock selectedFlock = ReadFlock.getFlockFromADate(conn, flockDate); // gets specified Flock POJO
        FlockDetails latestFlockDetail = ReadFlockDetails.getMostRecent(conn, flockDate); // latest Flock Detail

        int flockID = selectedFlock.getFlockId();
        int depleted = ReadFlockDetails.getCumulativeDepletedCount(conn, flockID); // gets cumulative depleted count from specified Flock
        int startingCount = selectedFlock.getStartingCount(); // gets starting count from specified Flock
        float mortalityRate = (float) startingCount / depleted * 100; // mortality rate of specified Flock
        int curCount = startingCount - depleted; // current count of specified Flock
        Date endDate = latestFlockDetail.getFdDate(); // gets Date of latest Flock Detail

        return new MortalityRate(mortalityRate, flockID, flockDate, endDate, startingCount, curCount);
    }

}
