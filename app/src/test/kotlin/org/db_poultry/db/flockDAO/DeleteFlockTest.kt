package org.db_poultry.db.flockDAO

import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.Date
import kotlin.test.*

class DeleteFlockTest {
    private var jdbcURL: String = "jdbc:postgresql://localhost:5432/db_poultry_test"
    private var conn: Connection

    init {
        DBConnect.init(jdbcURL, "db_poultry_test", "db_poultry_test")
        conn = DBConnect.getConnection()!!
        cleanTables(conn)
    }

    @Test
    fun testDeleteFlockWithDataOne() {
        val date =      Date.valueOf("1000-01-01")

        CreateFlock.createFlock(conn, 100, date)
        val result = DeleteFlock.undoCreateFlock(conn)
        assertEquals("DELETE FROM Flock WHERE ctid IN (SELECT ctid FROM Flock ORDER BY flock_id DESC LIMIT 1)", result)
        assertNull(ReadFlock.getFlockFromADate(conn, date))
    }

    fun testDeleteFlockWithDataTwo() {
        val dateOne = Date.valueOf("1000-01-02")
        val dateTwo = Date.valueOf("1000-01-01")

        CreateFlock.createFlock(conn, 100, dateOne)
        CreateFlock.createFlock(conn, 100, dateTwo)
        val result = DeleteFlock.undoCreateFlock(conn)
        assertEquals("DELETE FROM Flock ORDER BY flock_id DESC LIMIT 1", result)
        assertNull(ReadFlock.getFlockFromADate(conn, dateTwo))
    }

    fun testDeleteFlockWithNoData() {
        val result = DeleteFlock.undoCreateFlock(conn)
        assertNull(result)
    }
}