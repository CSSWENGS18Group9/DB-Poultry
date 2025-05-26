package org.db_poultry.controller

import org.db_poultry.db.flockDAO.GetFlocks
import org.db_poultry.db.flockDetailsDAO.CreateFlockDetails
import org.db_poultry.errors.generateErrorMessage
import org.db_poultry.pojo.FlockComplete
import java.sql.Connection
import java.sql.Date

// returns {1} if flock details have been recorded, {0} if not. {-1} if there is an error.
fun recordFlockDetails(
    connection: Connection?,
    flockSelectedDate: Date,
    flockDetailDate: Date,
    depletedCount: Int
): Int {
    if (connection == null) {
        generateErrorMessage(
            "Error at `recordFlockDetails()` in `flockDetailsController`.",
            "Connection is null",
            "Ensure that the connection has been established.",
        )

        return -1
    }

    // first verify if the Flock selected (given the timestamp) exists
    val flocks = GetFlocks.allByDate(connection)

    if (flocks.isEmpty()) {
        generateErrorMessage(
            "Error at `recordFlockDetails()` in `flockDetailsController`.",
            "Flock table is empty; `GetFlocks.allByDate` returned an empty HashSet.",
            "Ensure that there is data in the database."
        )

        return -1
    }

    val flockSelected: FlockComplete? = flocks.get<Date, FlockComplete>(flockSelectedDate)

    // input validations

    if (flockSelected == null) {
        // if flock selected (by date) doesn't exist
        return 0
    }

    if ((depletedCount < 0) or (depletedCount > flockSelected.flock.startingCount)) {
        // cant record flock detail if depleted count is negative
        // or depletedCount is greater than what we started with
        return 0
    }

    // FIXME: add more input validations here

    // call the DAO
    CreateFlockDetails.createFlockDetails(connection, flockSelected.flock.flockId, flockDetailDate, depletedCount)

    return 1
}
}