package org.db_poultry.db.flock_details

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.db_poultry.db.DBConnect
import org.db_poultry.App
import java.sql.Timestamp
import java.text.SimpleDateFormat

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
    fun testCreateFlockDetails() {
        val timestamp = Timestamp(SimpleDateFormat("yyyy-MM-dd").parse("1999-01-01").time)
        val result = Create.createFlockDetails(conn.conn, 1, timestamp, 0, 1)
        assertEquals(result, "INSERT INTO Flock_Details (Flock_ID, FD_Date, Current_Count, Depleted_Count) VALUES (1, $timestamp, 0, 1)", "succesfully created flock")
    }

    @Test
    fun testCreateFlockDetailsWithNegativeInput() {
        val timestamp = Timestamp(SimpleDateFormat("yyyy-MM-dd").parse("1999-01-01").time)
        val result = Create.createFlockDetails(conn.conn, -1, timestamp, -1, -1)
        assertNull(result)
    }

    @Test
    /*
        Tests if the user can enter in the first Flock Detail where there is zero curCount
     */
    fun testCreateFlockDetailsWithZeroCurCountStart() {
        val timestamp = Timestamp(SimpleDateFormat("yyyy-MM-dd").parse("1999-01-01").time)
        val result = Create.createFlockDetails(conn.conn, 1, timestamp, 1, 0)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithIncreasingCurCount() {
        val timestampOne = Timestamp(SimpleDateFormat("yyyy-MM-dd").parse("1999-01-01").time)
        val timestampTwo = Timestamp(SimpleDateFormat("yyyy-MM-dd").parse("1999-01-02").time)
        Create.createFlockDetails(conn.conn, 1, timestampOne, 1, 1)
        val result = Create.createFlockDetails(conn.conn, 1, timestampTwo, 1, 2)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithSameDateInSameFlockID() {
        val timestamp = Timestamp(SimpleDateFormat("yyyy-MM-dd").parse("1999-01-01").time)
        Create.createFlockDetails(conn.conn, 1, timestamp, 1, 1)
        val result = Create.createFlockDetails(conn.conn, 1, timestamp, 1, 2)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithMiscalculatedCurCount() {
        val timestampOne = Timestamp(SimpleDateFormat("yyyy-MM-dd").parse("1999-01-01").time)
        val timestampTwo = Timestamp(SimpleDateFormat("yyyy-MM-dd").parse("1999-01-02").time)
        Create.createFlockDetails(conn.conn, 1, timestampOne, 0, 10)
        val result = Create.createFlockDetails(conn.conn, 1, timestampTwo, 1, 8)
        assertNull(result)
    }
}


