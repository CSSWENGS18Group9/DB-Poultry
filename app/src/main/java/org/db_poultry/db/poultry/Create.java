package org.db_poultry.db.poultry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class Create {

    /**
     * Adds a new record into the Poultry table
     *
     * @param connect       the Connection thing with SQL
     * @param flockID       the ID of this flock
     * @param date          the starting date of this flock
     * @param depleted      the amount of dead chicken
     * @param curCount      the current amount of chickens
     * @return a String which is the query with filled-in values
     */
    public static String createPoultry(Connection connect, int poultryID, int flockID, Timestamp date, int depleted, int curCount) {
        String query = "INSERT INTO Flock_Details (Flock_Details_ID, Flock_ID, FD_Date, Current_Count, Depleted_Count) VALUES (?, ?, ?, ?, ?)"; // Query to be used in preparedStatement

        try {
            PreparedStatement preppedStatement = connect.prepareStatement(query); // preparedStatement for SQL stuff

            // Sets the values to be added
            preppedStatement.setInt(1, poultryID);
            preppedStatement.setInt(2, flockID);
            preppedStatement.setTimestamp(3, date);
            preppedStatement.setInt(4, depleted);
            preppedStatement.setInt(5, curCount);

            preppedStatement.executeUpdate(); // Executes query

            String query2 = preppedStatement.toString(); // Stores the value of preppedStatement as a String

            preppedStatement.close(); // Closes preparedStatement

            return query2; // Returns the copied preppedStatement as a String
        } catch (Exception e) {
            System.out.println("createPoultry() error!!");
            System.out.println(e.getMessage());
            return null;
        }
    }
}
