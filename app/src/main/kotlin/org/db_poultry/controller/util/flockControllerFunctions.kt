package org.db_poultry.controller.util

import org.db_poultry.db.flockDAO.CreateFlock
import org.db_poultry.db.flockDAO.ReadFlock
import org.db_poultry.errors.generateErrorMessage
import java.sql.Connection
import java.sql.Date

fun recordFlock(connection: Connection?, startCount: Int, inputDate: Date?): Int {
    if (connection == null) {
        generateErrorMessage(
            "Error at `recordFlock()` in `flockController`.",
            "Connection is null",
            "Ensure that the connection has been established."
        )
        return -1
    }

    if (startCount <= 0) {
        generateErrorMessage(
            "Error at `recordFlock()` in `flockController`.",
            "Invalid starting count: $startCount",
            "Starting count must be greater than 0."
        )
        return 0
    }

    // Default to today if inputDate is null
    val startDate: Date = inputDate ?: Date.valueOf(java.time.LocalDate.now())

    val flocksByDate = ReadFlock.allByDate(connection)

    if (flocksByDate.containsKey(startDate)) {
        // check if a flock with the same starting date already exists
        generateErrorMessage(
            "Error at `recordFlock()` in `flockController`.",
            "Duplicate starting date: $startDate",
            "A flock already exists for this date."
        )
        return 0
    }

    //NOTE: connection is of type Connection? and not Connection
    if (checkFlockDateInbetween(connection, startDate) != 0){
        generateErrorMessage(
            "Error at 'checkDateInbetween()' in `flockController`.",
            "Starting date: $startDate is in between a time span of another Flock",
            "Change starting date to a date not in between a time span of another Flock"
        )
        return 0
    }

    // Safe to insert
    CreateFlock.createFlock(connection, startCount, startDate)
    return 1
}


