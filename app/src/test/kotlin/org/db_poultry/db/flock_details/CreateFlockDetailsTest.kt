package org.db_poultry.db.flock_details

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.sql.Date

class CreateFlockDetailsTest {
    private var jdbcURL: String
    private var conn: DBConnect

    init {
        val app = App()

        app.getDotEnv()

        jdbcURL = "jdbc:postgresql://localhost:${app.databasePort}/${app.databaseName}"
        conn = DBConnect(jdbcURL, app.databaseName, app.databasePass)
    }

    @Test
    fun testCreateFlockDetailsValidInput() {
        val date = Date.valueOf("1000-01-01")

        val result = CreateFlockDetails.createFlockDetails(conn.getConnection(), 1, date, 0)

        assertEquals("INSERT INTO Flock_Details (Flock_ID, FD_Date, Depleted_Count) VALUES (1, 1000-01-01, 0)", result)
    }

    @Test
    fun testCreateFlockDetailsWithNegativeFlockID() {
        val date = Date.valueOf("1000-01-02")

        val result = CreateFlockDetails.createFlockDetails(conn.getConnection(), -1, date, 0)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithNegativeDepletedCount() {
        val date = Date.valueOf("1000-01-03")

        val result = CreateFlockDetails.createFlockDetails(conn.getConnection(), 1, date, -1)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithNegativeFlockIDAndDepletedCount() {
        val date = Date.valueOf("1000-01-04")

        val result = CreateFlockDetails.createFlockDetails(conn.getConnection(), -1, date, -1)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithSameDateInSameFlockID() {
        val date = Date.valueOf("1000-01-05")

        CreateFlockDetails.createFlockDetails(conn.getConnection(), 1, date, 1)
        val result = CreateFlockDetails.createFlockDetails(conn.getConnection(), 1, date, 1)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithSameDateInDifferentFlockID() {
        val date = Date.valueOf("1000-01-06")

        CreateFlockDetails.createFlockDetails(conn.getConnection(), 1, date, 1)
        val result = CreateFlockDetails.createFlockDetails(conn.getConnection(), 2, date, 1)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithDNEFlockID() {
        val date = Date.valueOf("1000-01-07")

        val result = CreateFlockDetails.createFlockDetails(conn.getConnection(), 1000, date, 1)
        assertNull(result)
    }
}

