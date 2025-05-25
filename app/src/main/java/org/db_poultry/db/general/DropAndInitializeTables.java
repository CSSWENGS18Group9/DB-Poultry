package org.db_poultry.db.general;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class DropAndInitializeTables {

    public static String initTables(Connection connect) {
        String completeQuery = "DROP TABLE IF EXISTS Flock, Flock_Details"; // First query that drops the Flock and Flock_Details tables

        try {
            Statement statement = connect.createStatement(); // statement for SQL stuff

            statement.execute(completeQuery); // Executes query

            completeQuery = "CREATE TABLE Flock (Flock_ID SERIAL PRIMARY KEY, Starting_Date DATE)"; // Second query that creates Flock table
            statement.execute(completeQuery); // Executes query

            completeQuery = "CREATE TABLE Flock_Details (Flock_Details_ID SERIAL PRIMARY KEY, Flock_ID SERIAL, FD_Date DATE, Current_Count INTEGER, Depleted_Count INTEGER, FOREIGN KEY (Flock_ID) REFERENCES Flock(Flock_ID))"; // Third query that creates Flock_Details table
            statement.execute(completeQuery); // Executes query

            statement.close(); // Closes statement

            return completeQuery; // Returns the query
        } catch (SQLException e) {
            generateErrorMessage("Error in `initTables()`.", "SQLException occurred.", "", e);
            return null;
        }
    }
}
