package org.db_poultry.db.flock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class Create {

    public static String createFlock(Connection connect, int flockID, Timestamp startDate) {
        String query = "INSERT INTO Flock (Flock_ID, Starting_Date) VALUES (?,?)"; // Query to be used in preparedStatement

        try {
            PreparedStatement preppedStatement = connect.prepareStatement(query); // preparedStatement for SQL stuff

            // Sets the values to be added
            preppedStatement.setInt(1, flockID);
            preppedStatement.setTimestamp(2, startDate);

            preppedStatement.executeUpdate(); // Executes query

            String query2 = preppedStatement.toString(); // Stores the value of preppedStatement as a String

            preppedStatement.close(); // Closes preparedStatement

            return query2; // Returns the copied preppedStatement as a String
        } catch (Exception e) {
            System.out.println("createFlock() error!!");
            System.out.println(e.getMessage());
            return null;
        }
    }
}
