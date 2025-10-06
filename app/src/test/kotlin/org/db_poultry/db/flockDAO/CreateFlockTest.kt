package org.db_poultry.db.flockDAO

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.db_poultry.db.DBConnect
import org.db_poultry.db.initDBAndUser
import org.db_poultry.db.initTables
import org.db_poultry.db.cleanAndInitTables
import java.sql.Connection
import java.sql.Date

class CreateFlockTest {
    private val jdbcURL = "jdbc:postgresql://localhost:5432/db_poultry_test"
    private val conn: Connection
    private val name = "db_poultry_test"
    init {
        initDBAndUser(name, name)

        DBConnect.init(jdbcURL, name, name)
        conn = DBConnect.getConnection()!!

        initTables(conn, name)
    }

    @Test
    fun testCreateFlockValidInputs() {
        val date = Date.valueOf("1000-01-01")
        val result = CreateFlock.createFlock(conn, 100, date)
        assertEquals("INSERT INTO Flock (Starting_Count, Starting_Date) VALUES (100, '1000-01-01')", result)
        cleanAndInitTables(conn, name)
    }

    @Test
    fun testCreateFlockWithSameDate() {
        val date = Date.valueOf("1000-01-02")
        CreateFlock.createFlock(conn, 100, date)

        val result = CreateFlock.createFlock(conn, 100, date)
        assertNull(result)
        cleanAndInitTables(conn, name)
    }

    @Test
    fun testCreateFlockWithZeroCount() {
        val date = Date.valueOf("1000-01-03")
        val result = CreateFlock.createFlock(conn, 0, date)
        assertNull(result)
        cleanAndInitTables(conn, name)
    }

    @Test
    fun testCreateFlockWithNegativeCount() {
        val date = Date.valueOf("1000-01-04")
        val result = CreateFlock.createFlock(conn, -1, date)
        assertNull(result)
        cleanAndInitTables(conn, name)
    }
}