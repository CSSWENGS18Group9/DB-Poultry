package org.db_poultry.db.flockDetailsDAO

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.db.flockDAO.CreateFlock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.sql.Connection
import java.sql.Date


class DeleteFlockDetailTest {
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
    fun testDeleteFlockDetailWithData() {
        val flockOneDate = Date.valueOf("1000-01-01")
        val flockTwoDate = Date.valueOf("1000-06-01")
        val fdDateOne = Date.valueOf("1000-01-01")
        val fdDateTwo = Date.valueOf("1000-03-01")
        val fdDateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 999, flockOneDate)
        CreateFlock.createFlock(conn, 999, flockTwoDate)

        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateOne, 10)
        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, fdDateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateThree, 10)

        val result = DeleteFlockDetail.undoCreateFlockDetail(conn, 1, fdDateTwo)
        assertNull(result)

        val resultTwo = DeleteFlockDetail.undoCreateFlockDetail(conn, 0, fdDateOne)
        assertNull(resultTwo)

        val resultThree = DeleteFlockDetail.undoCreateFlockDetail(conn, 0, fdDateThree)
        assertEquals("DELETE FROM Flock_Details WHERE Flock_ID = 1 AND FD_Date = 1000-04-01", resultThree)

        val flockDetail = ReadFlockDetails.getMostRecent(conn, flockOneDate)
        assertEquals(fdDateOne, flockDetail.fdDate)

    }

    @Test
    fun testDeleteFlockDetailWithWrongFlockId() {
        val flockOneDate = Date.valueOf("1000-01-01")
        val flockTwoDate = Date.valueOf("1000-06-01")
        val fdDateOne = Date.valueOf("1000-01-01")
        val fdDateTwo = Date.valueOf("1000-03-01")
        val fdDateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 999, flockOneDate)
        CreateFlock.createFlock(conn, 999, flockTwoDate)

        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateOne, 10)
        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, fdDateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateThree, 10)

        val result = DeleteFlockDetail.undoCreateFlockDetail(conn, 3, fdDateTwo)
        assertNull(result)


    }


    @Test
    fun testDeleteFlockDetailWithNoData() {

        val flockOneDate = Date.valueOf("1000-01-01")

        CreateFlock.createFlock(conn, 999, flockOneDate)

        val result = DeleteFlockDetail.undoCreateFlockDetail(conn, 0, flockOneDate)

        assertNull(result)
    }
}