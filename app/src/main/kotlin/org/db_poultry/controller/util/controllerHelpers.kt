package org.db_poultry.controller.util

import org.db_poultry.db.flockDAO.ReadFlock
import org.db_poultry.errors.generateErrorMessage
import org.db_poultry.pojo.FlockPOJO.FlockComplete
import java.sql.Connection
import java.sql.Date
import java.sql.ResultSet
import java.sql.SQLException

/**
 * Checks date in between a Starting Date and the last Flock Detail date
 *
 * @param connect    the Connection thing with SQL
 * @param inputtedDate the inputted Flock Detail date
 * @param flockDate  the Starting Date of a Flock for Detail validity
 * @return a String which is the query with filled-in values
 */
fun checkDateInbetween(connect: Connection, inputtedDate: Date, flockDate: Date? = null, forNewFlock: Boolean = false): Int {

    var dateRangeQuery: String? = null
    var nextStartDate: Date? = null
    val overlapsQuery = "SELECT COUNT(*) AS overlaps FROM Flock LEFT JOIN (SELECT Flock_ID, MAX(FD_Date) as endDate FROM Flock_Details GROUP BY Flock_ID) Details ON Flock.Flock_ID = Details.Flock_ID WHERE ? BETWEEN Flock.Starting_Date AND COALESCE(Details.endDate, Flock.Starting_Date)"

    if (!forNewFlock) {
        dateRangeQuery = "SELECT MIN(Starting_Date) AS nextStartDate FROM Flock WHERE Starting_Date > ?"
    }

    try {
        val result: ResultSet
        var dateRangeResult: ResultSet?

        if (!forNewFlock) { // for New Flock Detail
            print("rightarea1")
            val preppedStatement1 = connect.prepareStatement(dateRangeQuery)
            preppedStatement1.setDate(1, flockDate)
            dateRangeResult = preppedStatement1.executeQuery()

            if (dateRangeResult.next()) {
                nextStartDate = dateRangeResult.getDate("nextStartDate")
            }

            dateRangeResult.close()
            preppedStatement1.close()

            val preppedStatement2 = connect.prepareStatement(overlapsQuery)
            preppedStatement2.setDate(1, inputtedDate)
            result = preppedStatement2.executeQuery()

            var overlap = 0


            if (inputtedDate != flockDate) {
                while (result.next()) {
                    overlap = result.getInt("overlaps")
                }
            }

            result.close()
            preppedStatement2.close()

            if (nextStartDate != null && inputtedDate.after(nextStartDate)) {
                return 1
            }

            return overlap
        } else { // for New Flock
            val preppedStatement = connect.prepareStatement(overlapsQuery)
            preppedStatement.setDate(1, inputtedDate)
            result = preppedStatement.executeQuery()

            var overlap = 0

            while (result.next()) {
                overlap = result.getInt("overlaps")
            }

            result.close()
            preppedStatement.close()

            return overlap
        }
    } catch (e: SQLException) {
        generateErrorMessage("Error in checkDateInbetween().", "SQLException occurred.", "", e)
        return -1
    }
}