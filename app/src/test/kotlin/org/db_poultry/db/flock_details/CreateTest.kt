package org.db_poultry.db.flock_details

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.db_poultry.db.DBConnect
import org.db_poultry.App
import java.sql.Date

class CreateTest {
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
        val date =      Date.valueOf("1999-01-01")

        val result = Create.createFlockDetails(conn.conn, 1, date, 0)

        assertEquals("INSERT INTO Flock_Details (Flock_ID, FD_Date, Depleted_Count) VALUES (1, 1999-01-01, 0)", result)
    }

    @Test
    fun testCreateFlockDetailsWithNegativeFlockID() {
        val date =      Date.valueOf("1999-01-02")

        val result = Create.createFlockDetails(conn.conn, -1, date, 0)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithNegativeDepletedCount() {
        val date =      Date.valueOf("1999-01-03")

        val result = Create.createFlockDetails(conn.conn, 1, date, -1)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithNegativeFlockIDandDepletedCount() {
        val date =      Date.valueOf("1999-01-04")

        val result = Create.createFlockDetails(conn.conn, -1, date, -1)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithSameDateInSameFlockID() {
        val date =      Date.valueOf("1999-01-05")

                        Create.createFlockDetails(conn.conn, 1, date, 1)
        val result =    Create.createFlockDetails(conn.conn, 1, date, 1)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithSameDateInDifferentFlockID() {
        val date =      Date.valueOf("1999-01-06")

                        Create.createFlockDetails(conn.conn, 1, date, 1)
        val result =    Create.createFlockDetails(conn.conn, 2, date, 1)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithDNEFlockID() {
        val date =      Date.valueOf("1999-01-07")

        val result = Create.createFlockDetails(conn.conn, 1000, date, 1)
        assertNull(result)
    }
}


