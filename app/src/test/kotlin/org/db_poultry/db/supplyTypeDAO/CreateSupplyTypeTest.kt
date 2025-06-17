package org.db_poultry.db.supplyTypeDAO;

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.Date
import java.sql.SQLException

import org.db_poultry.db.supplyTypeDAO.CreateSupplyType

// Test class for CreateSupplyType
// This class tests the functionality of creating supply types in the database.
// Restrictions include:
// - Supply type names must be unique.
// - Supply type names cannot be empty.
// - Unit cannot be empty.

class CreateSupplyTypeTest {
    private var jdbcURL: String
    private var conn: Connection

    init {
        val app = App()

        app.getDotEnv()

        jdbcURL = "jdbc:postgresql://localhost:${app.databasePort}/${app.databaseName}"
        DBConnect.init(jdbcURL, app.databaseName, app.databasePass)
        conn = DBConnect.getConnection()!!
        cleanTables(conn)
    }

    @Test
    fun testCreateSupplyTypeValidInputs() {
        val result = CreateSupplyType.createSupplyType(conn, "Feed", "kg")
        assertEquals("INSERT INTO supply_type (supply_name, unit) VALUES(Feed, kg)", result)
    }

    @Test
    fun testCreateSupplyTypeWithSameName() {
        CreateSupplyType.createSupplyType(conn, "Water", "liters")
        val result = CreateSupplyType.createSupplyType(conn, "Water", "liters")
        assertNull(result)
    }

    @Test
    fun testCreateSupplyTypeWithEmptyName() {
        val result = CreateSupplyType.createSupplyType(conn, "", "liters")
        assertNull(result)
    }

    @Test
    fun testCreateSupplyTypeWithEmptyUnit() {
        val result = CreateSupplyType.createSupplyType(conn, "Feed", "")
        assertNull(result)
    }
}

