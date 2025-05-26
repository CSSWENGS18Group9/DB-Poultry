package org.db_poultry.db.flock_detailsDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class CreateFlockDetails {

    /**
     * Adds a new record into the Flock_Details table
     *
     * @param connect  the Connection thing with SQL
     * @param date     the starting date of this flock
     * @param depleted the amount of dead chickens
     * @param flockID  the flock ID this poultry is connected to
     * @return a String which is the query with filled-in values
     */
    public static String createFlockDetails(Connection connect, int flockID, Date date, int depleted) {
        //🤡
        if (depleted < 0) {
            generateErrorMessage("Error in `createFlockDetails()`.", "Depleted amount is less than 0.", "", null);
            return null;
        }

        String completeQuery = "INSERT INTO Flock_Details (Flock_ID, FD_Date, Depleted_Count) VALUES (" + flockID + ", " + date + ", " + depleted + ")"; // Query filled in to be returned
        String incompleteQuery = "INSERT INTO Flock_Details (Flock_ID, FD_Date, Depleted_Count) VALUES (?, ?, ?)"; // Query to be used in preparedStatement

        try {
            PreparedStatement preppedStatement = connect.prepareStatement(incompleteQuery); // preparedStatement for SQL stuff

            // Sets the values to be added
            preppedStatement.setInt(1, flockID);
            preppedStatement.setDate(2, date);
            preppedStatement.setInt(3, depleted);

            preppedStatement.executeUpdate(); // Executes query

            preppedStatement.close(); // Closes preparedStatement

            return completeQuery; // Returns the filled-in query
        } catch (SQLException e) {
            generateErrorMessage("Error in `createFlockDetails()`.", "SQLException occurred.", "", e);
            return null;
        }
    }
}