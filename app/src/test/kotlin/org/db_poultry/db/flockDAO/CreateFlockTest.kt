package org.db_poultry.db.flockDAO

import com.sun.jdi.connect.Connector
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.db_poultry.db.DBConnect
import org.db_poultry.App
import org.db_poultry.db.cleanTables
import java.sql.Connection
import java.sql.Date

class CreateFlockTest {
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
    fun testCreateFlockValidInputs() {
        val date =      Date.valueOf("1000-01-01")

        val result =    CreateFlock.createFlock(conn, 100, date)

        assertEquals("INSERT INTO Flock (Starting_Count, Starting_Date) VALUES (100, 1000-01-01)", result)
    }

    @Test
    fun testCreateFlockWithSameDate() {
        val date =      Date.valueOf("1000-01-02")

        CreateFlock.createFlock(conn, 100, date)
        val result =    CreateFlock.createFlock(conn, 100, date)

        assertNull(result)
    }

    @Test
    fun testCreateFlockWithZeroCount() {
        val date =      Date.valueOf("1000-01-03")

        val result =    CreateFlock.createFlock(conn, 0, date)

        assertNull(result)
    }

    @Test
    fun testCreateFlockWithNegativeCount() {
        val date =      Date.valueOf("1000-01-04")

        val result =    CreateFlock.createFlock(conn, -1, date)

        assertNull(result)
    }
}