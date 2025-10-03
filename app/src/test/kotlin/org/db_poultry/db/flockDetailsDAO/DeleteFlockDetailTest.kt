package org.db_poultry.db.flockDetailsDAO

import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanAndInitTables
import org.db_poultry.db.initDBAndUser
import org.db_poultry.db.initTables
import org.db_poultry.db.flockDAO.CreateFlock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.Date


class DeleteFlockDetailTest {
    private val jdbcURL = "jdbc:postgresql://localhost:5432/db_poultry_test"
    private val conn: Connection

    init {
        initDBAndUser()

        DBConnect.init(jdbcURL, "db_poultry_test", "db_poultry_test")
        conn = DBConnect.getConnection()!!

        initTables(conn)
    }

    @Test
    fun testDeleteFlockDetailWithData() {
        val flockOneDate = Date.valueOf("1000-01-01")
        val flockTwoDate = Date.valueOf("1000-06-01")
        val fdDateOne = Date.valueOf("1000-01-01")
        val fdDateTwo = Date.valueOf("1000-07-01")
        val fdDateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 999, flockOneDate)
        CreateFlock.createFlock(conn, 999, flockTwoDate)

        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateOne, 10)
        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, fdDateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateThree, 10)

        val resultThree = DeleteFlockDetail.undoCreateFlockDetail(conn, 1, fdDateThree)
        assertEquals("DELETE FROM Flock_Details WHERE Flock_ID = 1 AND FD_Date = 1000-04-01", resultThree)

        val flockDetail = ReadFlockDetails.getMostRecent(conn, flockOneDate)
        assertEquals(fdDateOne, flockDetail.fdDate)

        cleanAndInitTables(conn)
    }

}