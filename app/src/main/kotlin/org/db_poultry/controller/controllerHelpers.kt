package org.db_poultry.controller

import org.db_poultry.db.flockDAO.ReadFlock
import org.db_poultry.errors.generateErrorMessage
import org.db_poultry.pojo.FlockComplete
import java.sql.Connection

fun <K> validateAndRead(
    connection: Connection?, reader: (Connection) -> HashMap<K, FlockComplete>?, callerName: String
): HashMap<K, FlockComplete>? {
    if (connection == null) {
        generateErrorMessage(
            "Error at `$callerName` in `flockDetailsController`.",
            "Connection is null",
            "Ensure that the connection has been established."
        )
        return null
    }

    val flocks = reader(connection)

    if (flocks == null || flocks.isEmpty()) {
        generateErrorMessage(
            "Error at `$callerName` in `flockDetailsController`.",
            "Returned data is null or empty.",
            "Ensure that there is data in the database."
        )
        return null
    }

    return flocks
}

fun validateAndReadByID(connection: Connection?) =
    validateAndRead(connection, ReadFlock::allByID, "validateAndReadByID")

fun validateAndReadByDates(connection: Connection?) =
    validateAndRead(connection, ReadFlock::allByDate, "validateAndReadByDates")
