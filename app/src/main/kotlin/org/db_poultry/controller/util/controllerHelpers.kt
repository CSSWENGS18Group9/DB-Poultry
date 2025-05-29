package org.db_poultry.controller.util

import org.db_poultry.db.flockDAO.ReadFlock
import org.db_poultry.errors.generateErrorMessage
import org.db_poultry.pojo.FlockComplete
import java.sql.Connection
import java.sql.Date
import java.sql.SQLException

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

fun checkDateInbetween(connect: Connection, inputtedDate: Date): Int {
    val incompleteQuery = "SELECT COUNT(*) AS overlaps FROM Flock LEFT JOIN (SELECT Flock_ID, MAX(FD_Date) as endDate FROM Flock_Details GROUP BY Flock_ID) Details ON Flock.Flock_ID = Details.Flock_ID WHERE ? BETWEEN Flock.Starting_Date AND COALESCE(Details.endDate, Flock.Starting_Date)" // Query to be used in preparedStatement
    // If it returns anything other than a 0 then there's overlap

    try {
        val preppedStatement = connect.prepareStatement(incompleteQuery) // preparedStatement for SQL stuff

        // Sets the values to be added
        preppedStatement.setDate(1, inputtedDate)

        val result = preppedStatement.executeQuery() // Executes query and stores it into a ResultSet

        var overlap = 0 // Gets number of overlap

        while (result.next()) { // Gets results per row from the SQL output
            overlap = result.getInt("overlaps") // Gets the output from the column I named Total_Count_Depleted
        }

        result.close() // CLoses result
        preppedStatement.close() // Closes preparedStatement

        return overlap // Returns the total depleted count
    } catch (e: SQLException) {
        generateErrorMessage("Error in checkDateInbetween().", "SQLException occurred.", "", e)
        return -1
    }
}