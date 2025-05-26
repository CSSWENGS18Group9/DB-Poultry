package org.db_poultry.db.flockDAO

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.db_poultry.db.DBConnect
import org.db_poultry.App
import java.sql.Date

class CreateFlockTest {
    private var jdbcURL: String
    private var conn: DBConnect

    init {
        val app = App()

        app.getDotEnv()

        jdbcURL = "jdbc:postgresql://localhost:${app.databasePort}/${app.databaseName}"
        conn = DBConnect(jdbcURL, app.databaseName, app.databasePass)
    }

    @Test
    fun testCreateFlockValidInputs() {
        val date =      Date.valueOf("1000-01-01")

        val result =    CreateFlock.createFlock(conn.getConnection(), 100, date)

        assertEquals("INSERT INTO Flock (Starting_Count, Starting_Date) VALUES (100, 1000-01-01)", result)
    }

    @Test
    fun testCreateFlockWithSameDate() {
        val date =      Date.valueOf("1000-01-02")

        CreateFlock.createFlock(conn.getConnection(), 100, date)
        val result =    CreateFlock.createFlock(conn.getConnection(), 100, date)

        assertNull(result)
    }

    @Test
    fun testCreateFlockWithZeroCount() {
        val date =      Date.valueOf("1000-01-03")

        val result =    CreateFlock.createFlock(conn.getConnection(), 0, date)

        assertNull(result)
    }

    @Test
    fun testCreateFlockWithNegativeCount() {
        val date =      Date.valueOf("1000-01-04")

        val result =    CreateFlock.createFlock(conn.getConnection(), -1, date)

        assertNull(result)
    }
}