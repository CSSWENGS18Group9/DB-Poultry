package org.db_poultry.db.flockDetailsDAO

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.db.flockDAO.CreateFlock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.Date

class CreateFlockDetailsTest {
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
    fun testCreateFlockDetailsValidInput() {
        val date = Date.valueOf("1000-01-01")

        CreateFlock.createFlock(conn, 999, date)

        val result = CreateFlockDetails.createFlockDetails(conn, 1, date, 0)

        assertEquals("INSERT INTO Flock_Details (Flock_ID, FD_Date, Depleted_Count) VALUES (1, 1000-01-01, 0)", result)
    }

    @Test
    fun testCreateFlockDetailsWithNegativeFlockID() {
        val date = Date.valueOf("1000-01-02")

        val result = CreateFlockDetails.createFlockDetails(conn, -1, date, 0)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithNegativeDepletedCount() {
        val date = Date.valueOf("1000-01-03")

        val result = CreateFlockDetails.createFlockDetails(conn, 1, date, -1)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithNegativeFlockIDAndDepletedCount() {
        val date = Date.valueOf("1000-01-04")

        val result = CreateFlockDetails.createFlockDetails(conn, -1, date, -1)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithSameDateInSameFlockID() {
        val date = Date.valueOf("1000-01-05")

        CreateFlockDetails.createFlockDetails(conn, 1, date, 1)
        val result = CreateFlockDetails.createFlockDetails(conn, 1, date, 1)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithSameDateInDifferentFlockID() {
        val date = Date.valueOf("1000-01-06")

        CreateFlockDetails.createFlockDetails(conn, 1, date, 1)
        val result = CreateFlockDetails.createFlockDetails(conn, 2, date, 1)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithDNEFlockID() {
        val date = Date.valueOf("1000-01-07")

        val result = CreateFlockDetails.createFlockDetails(conn, 1000, date, 1)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithDepletedCountOverStartCount() {
        val dateOne = Date.valueOf("1000-08-01")
        val dateTwo = Date.valueOf("1000-09-01")
        val dateThree = Date.valueOf("1000-10-01")

        CreateFlock.createFlock(conn, 15, dateOne)

        CreateFlockDetails.createFlockDetails(conn, 1, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, 1, dateTwo, 10)
        val result = CreateFlockDetails.createFlockDetails(conn, 1, dateThree, 15)

        assertNull(result)
    }
}

