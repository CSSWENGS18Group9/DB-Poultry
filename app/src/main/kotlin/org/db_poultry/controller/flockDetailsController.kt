package org.db_poultry.controller

import org.db_poultry.db.flockDAO.ReadFlock
import org.db_poultry.db.flockDetailsDAO.CreateFlockDetails
import org.db_poultry.errors.generateErrorMessage
import org.db_poultry.pojo.FlockComplete
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
        // if flock selected (by date) doesn't exist
        return 0
    }

    if ((depletedCount < 0) || (depletedCount > flockSelected.flock.startingCount)) {
        // cant record flock detail if depleted count is negative
        // or depletedCount is greater than what we started with
        return 0
    }

    // FIXME: add more input validations here

    // call the DAO
    CreateFlockDetails.createFlockDetails(connection, flockSelected.flock.flockId, detailDate, depletedCount)

    return 1
}
