package org.db_poultry.controller.util

import org.db_poultry.db.DBConnect
import org.db_poultry.db.flockDAO.ReadFlock
import org.db_poultry.db.flockDetailsDAO.CreateFlockDetails
import org.db_poultry.db.flockDetailsDAO.DepletedCount
import org.db_poultry.db.flockDetailsDAO.ReadFlockDetails
import org.db_poultry.errors.generateErrorMessage
import org.db_poultry.pojo.FlockComplete
import org.db_poultry.pojo.FlockDetails
import java.sql.Connection


// returns {1} if flock details have been recorded, {0} if not. {-1} if there is an error.
fun recordFlockDetails(
    connection: Connection?,
    flockSelectedDate: java.sql.Date?,
    flockDetailDate: java.sql.Date?,
    depletedCount: Int
): Int {
    val flocks = validateAndReadByDates(connection)

    if (flocks == null){
        return -1
    }

    // if flockSelectedDate is null
    val selectedDate: java.sql.Date = flockSelectedDate ?: java.sql.Date.valueOf(java.time.LocalDate.now())

    // if flockDetailDate is null
    val detailDate: java.sql.Date = flockDetailDate ?: java.sql.Date.valueOf(java.time.LocalDate.now())

    // get the FlockComplete POJO in the flocks; if not in flocks then null
    val flockSelected: FlockComplete? = flocks.get<java.sql.Date, FlockComplete>(selectedDate)

    if (flockSelected == null) {
        generateErrorMessage("Error at 'recordFlockDetails()' in 'flockDetailsController'.",
            "A flock does not exist on this date: $selectedDate",
            "Select a flock that exists on this date."
        )
        // if flock selected (by date) doesn't exist
        return 0
    }

    if ((depletedCount < 0) || (depletedCount > flockSelected.flock.startingCount)) {
        generateErrorMessage("Error at 'recordFlockDetails()' in 'flockDetailsController'.",
            "Depleted count ($depletedCount) is negative or is greater than the starting flock count",
            "Use a valid depleted count."
        )
        // cant record flock detail if depleted count is negative
        // or depletedCount is greater than what we started with
        return 0
    }

    if (DepletedCount.getCumulativeDepletedCount(connection, flockSelected.flock.flockId) > flockSelected.flock.startingCount) {
        generateErrorMessage("Error at 'recordFlockDetails()' in 'flockDetailsController'.",
            "Total depleted count (${DepletedCount.getCumulativeDepletedCount(connection, flockSelected.flock.flockId)}) would be greater than the starting flock count",
            "Use a valid depleted count."
        )
        // check if adding the current depletedCount would exceed the starting count
        // if it would, reject the input to prevent invalid data
        return 0
    }

    val recentFD = ReadFlockDetails.getMostRecent(connection,flockSelectedDate)

    if (recentFD != null) {
        val recentFDdate: java.sql.Date = recentFD.fdDate

        val flockDetailsList: List<FlockDetails> = ReadFlock.getFlockDetailsFromDate(DBConnect.getConnection(), flockSelectedDate, flockSelectedDate,recentFDdate)

        for (i in flockDetailsList) {
            if (i.fdDate == detailDate) {
                generateErrorMessage("Error at 'recordFlockDetails()' in 'flockDetailsController'.",
                    "Date $detailDate happens on or before the latest flock detail $recentFDdate",
                    "Use a date that happens after the most recent flock detail."
                )
                // check if the date for the flock_detail is before or the exact date as the recent flock detail in the db
                return 0
            }
        }
    }


    val flockSelectedDate = flockSelected.flock.startingDate
    if (flockSelectedDate.compareTo(detailDate) > 0) {
        generateErrorMessage("Error at 'recordFlockDetails()' in 'flockDetailsController'.",
            "Date $detailDate happens before the flock was entered in the Database $flockSelectedDate",
            "Use a date that happens after the most recent flock detail."
        )
        // check if the date for the flock_detail is before the flock was entered in the Database
        return 0
    }

    // FIXME: add more input validations here
    // i don't think there's any more inputs that could be checked

    // call the DAO
    CreateFlockDetails.createFlockDetails(connection, flockSelected.flock.flockId, detailDate, depletedCount)

    return 1
}
