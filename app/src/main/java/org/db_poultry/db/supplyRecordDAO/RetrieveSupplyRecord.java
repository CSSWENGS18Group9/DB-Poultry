package org.db_poultry.db.supplyRecordDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class RetrieveSupplyRecord {

    public static String retrieveSupply(Connection conn, Date retrieveDate, String supplyName) {

        try (PreparedStatement preppedStatement = conn.prepareStatement("""
              UPDATE Supply_Record sr
              SET Retrieved = TRUE
              FROM Supply_Type st
              WHERE sr.Supply_Type_ID = st.Supply_Type_ID AND sr.Retrieved = FALSE AND sr.SR_Date = ? AND st.Supply_Name = ?;
              """)) {

            // Sets the values to be added
            preppedStatement.setDate(1, retrieveDate);
            preppedStatement.setString(2, supplyName);
            preppedStatement.executeUpdate(); // Executes query

            return String.format(
                    "UPDATE Supply_Record sr SET Retrieved = TRUE FROM Supply_Type st WHERE sr.Supply_Type_ID = st.Supply_Type_ID AND sr.Retrieved = FALSE AND sr.SR_Date = %s AND st.Supply_Name = %s;", retrieveDate.toString(), supplyName);
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `retrieveSupply()`.",
                    "SQLException occurred.",
                    "",
                    e);
            return null;
        }

    }
}
