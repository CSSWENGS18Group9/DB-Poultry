package org.db_poultry.db.flockDetailsDAO;

import org.db_poultry.db.flockDAO.ReadFlock;
import org.db_poultry.pojo.FlockComplete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class DepletedCount {

    /**
     * Returns the total depleted count from a specified Flock ID
     *
     * @param connect the Connection thing with SQL
     * @param flockID the flock ID this poultry is connected to
     * @return an Int which is the total depleted count
     */
    public static int getCumulativeDepletedCount(Connection connect, int flockID) {
        String incompleteQuery = "SELECT SUM(Depleted_Count) AS Total_Count_Depleted FROM Flock_Details WHERE Flock_ID = ?"; // Query to be used in preparedStatement

        HashMap<Integer, FlockComplete> flocks = ReadFlock.allByID(connect);
        FlockComplete flockChosen = flocks.get(flockID);
        int flockStartingCount = flockChosen.getFlock().getStartingCount();

        try {
            PreparedStatement preppedStatement = connect.prepareStatement(incompleteQuery); // preparedStatement for SQL stuff



            // Sets the values to be added
            preppedStatement.setInt(1, flockID);

            ResultSet result = preppedStatement.executeQuery(); // Executes query and stores it into a ResultSet

            int sum = 0; // Gets total depleted count

            while (result.next()) { // Gets results per row from the SQL output
                sum = result.getInt("Total_Count_Depleted"); // Gets the output from the column I named Total_Count_Depleted
            }

            result.close(); // CLoses result
            preppedStatement.close(); // Closes preparedStatement

            if(flockStartingCount < sum) {
                return -1;
            }

            return sum; // Returns the total depleted count

        } catch (SQLException e) {
            generateErrorMessage("Error in `cumulativeDepletedCount()`.", "SQLException occurred.", "", e);
            return -1;
        }

    }
}
