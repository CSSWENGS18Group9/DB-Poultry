package org.db_poultry.db.reportDAO;
import org.db_poultry.db.flockDetailsDAO.ReadFlockDetails;
import org.db_poultry.db.flockDAO.ReadFlock;
import org.db_poultry.pojo.FlockPOJO.Flock;
import org.db_poultry.pojo.FlockPOJO.FlockDetails;
import org.db_poultry.pojo.ReportPOJO.MortalityRate;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class GetMortalityRate {

    /**
     * Gets the mortality rate for a specified flock
     *
     * @param conn          the Connection thing with SQL
     * @param flockDate     the starting date of this flock
     * @return a MortalityRate object
     */
    public static MortalityRate calculateMortalityRateForFlock(Connection conn, Date flockDate) {

        Flock selectedFlock = ReadFlock.getFlockFromADate(conn, flockDate); // gets specified Flock POJO
        if (selectedFlock == null) {
            generateErrorMessage("Error in `calculateMortalityRateForFlock()` in `GetMortalityRate`.", "No flock found for the specified date", "", null);
            return null;
        }

        FlockDetails latestFlockDetail = ReadFlockDetails.getMostRecent(conn, flockDate); // latest Flock Detail

        int flockID = selectedFlock.getFlockId(); // gets ID of specified Flock
        int depleted = ReadFlockDetails.getCumulativeDepletedCount(conn, flockID); // gets cumulative depleted count from specified Flock
        int startingCount = selectedFlock.getStartingCount(); // gets starting count from specified Flock

        if (latestFlockDetail == null) {
            return new MortalityRate(0, flockID, flockDate, startingCount, startingCount);
        }

        if (startingCount == 0) {
            generateErrorMessage("Error in `calculateMortalityRateForFlock()` in `GetMortalityRate`.", "Cannot divide by zero", "", null);
            return null;
        }

        int curCount = startingCount - depleted; // current count of specified Flock
        float mortalityRate = (float) depleted / startingCount * 100; // mortality rate of specified Flock
        Date endDate = latestFlockDetail.getFdDate(); // gets Date of latest Flock Detail

        return new MortalityRate(mortalityRate, flockID, flockDate, endDate, startingCount, curCount); // returns an instance of MortalityRate

    }

    /**
     * Gets the mortality rate for a day with the current count
     *
     * @param conn          the Connection thing with SQL
     * @param flockDate     the starting date of this flock
     * @param targetDate    the "end date" for calculating the mortality rate
     * @return a MortalityRate object
     */
    public static MortalityRate calculateMortalityRateForFlockDate(Connection conn, Date flockDate, Date targetDate) {

        Flock selectedFlock = ReadFlock.getFlockFromADate(conn, flockDate); // gets specified Flock POJO
        if (selectedFlock == null) {
            generateErrorMessage("Error in `calculateMortalityRateForFlock()` in `GetMortalityRate`.", "No flock found for the specified date", "", null);
            return null;
        }

        int flockID = selectedFlock.getFlockId(); // gets ID of specified Flock
        int cumDepleted = ReadFlockDetails.getCumulativeDepletedCountUpToDate(conn, flockID, targetDate); // gets cumulative depleted count up until before the target Date
        int startingCount = selectedFlock.getStartingCount(); // gets starting count from specified Flock
        int curCountOnDay = startingCount - cumDepleted; // current count up until before the target Date

        if (curCountOnDay == 0) {
            generateErrorMessage("Error in `calculateMortalityRateForFlock()` in `GetMortalityRate`.", "Cannot divide by zero", "", null);
            return null;
        }

        FlockDetails targetDetail = null;
        List<FlockDetails> targetDetailList = ReadFlockDetails.getFlockDetailsFromDate(conn, flockDate, targetDate, targetDate);

        if (targetDetailList != null && !targetDetailList.isEmpty()) {
            targetDetail = targetDetailList.getFirst();
        }
        else {
            generateErrorMessage("Error in `calculateMortalityRateForFlockDate()` in `GetMortalityRate`.", "No flock details found for the specified date", "", null);
            return null;
        }

        int depleted = targetDetail.getDepletedCount();

        float mortalityRate = (float) depleted / curCountOnDay * 100; // mortality rate of specified Flock

        FlockDetails latestFlockDetail = ReadFlockDetails.getMostRecent(conn, flockDate);

        return new MortalityRate(mortalityRate, flockID, flockDate, latestFlockDetail.getFdDate(), startingCount, curCountOnDay); // returns an instance of MortalityRate
    }

}
