package org.db_poultry

import java.sql.*

fun addSupply(connect: Connection, supplyID: String, quantity: Int, unit: Int, type: Int): Int {
    val query = "INSERT INTO Supply (Supply_ID, Quantity, Unit, Type) VALUES (?, ?, ?, ?)" // Query to be used in preparedStatement

    val preppedStatement = connect.prepareStatement(query) // preparedStatement for SQL stuff

    // Sets the values to be added
    preppedStatement.setString(1, supplyID)
    preppedStatement.setInt(2, quantity)
    preppedStatement.setInt(3, unit)
    preppedStatement.setInt(4, type)

    val success = preppedStatement.executeUpdate() // Executes query

    preppedStatement.close() // Closes preparedStatement

    return success // Returns 1 if success
}

fun updateSupply(connect: Connection, supplyID: String, quantity: Int, unit: Int, type: Int): Int {
    val query = "UPDATE Supply SET Quantity = ?, unit = ?, type = ? WHERE Supply_ID = ?" // Query to be used in preparedStatement

    val preppedStatement = connect.prepareStatement(query) // preparedStatement for SQL stuff

    // Sets the values to be updated
    preppedStatement.setInt(1, quantity)
    preppedStatement.setInt(2, unit)
    preppedStatement.setInt(3, type)
    preppedStatement.setString(4, supplyID)

    val success = preppedStatement.executeUpdate() // Executes query

    preppedStatement.close() // Closes preparedStatement

    return success // Returns 1 if success
}