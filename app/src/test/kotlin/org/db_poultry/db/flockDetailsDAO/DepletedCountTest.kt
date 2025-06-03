package org.db_poultry.db.flockDetailsDAO

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.db.flockDAO.CreateFlock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.Date

class DepletedCountTest {

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
    fun testDepletedCountValidInput() {
        val dateOne = Date.valueOf("1000-05-01")
        val dateTwo = Date.valueOf("1000-06-01")
        val dateThree = Date.valueOf("1000-07-01")

        CreateFlock.createFlock(
            conn, 100, dateOne
        )

        CreateFlockDetails.createFlockDetails(
            conn, 1, dateOne, 5
        )
        CreateFlockDetails.createFlockDetails(
            conn, 1, dateTwo, 10
        )
        CreateFlockDetails.createFlockDetails(
            conn, 1, dateThree, 15
        )

        val result = DepletedCount.getCumulativeDepletedCount(
            conn, 1
        )
        Assertions.assertEquals(30, result)
    }

    @Test
    fun testDepletedCountOverStartCount() {
        val dateOne = Date.valueOf("1000-08-01")
        val dateTwo = Date.valueOf("1000-09-01")
        val dateThree = Date.valueOf("1000-10-01")

        CreateFlock.createFlock(
            conn, 15, dateOne
        )

        CreateFlockDetails.createFlockDetails(
            conn, 1, dateOne, 5
        )
        CreateFlockDetails.createFlockDetails(
            conn, 1, dateTwo, 10
        )
        CreateFlockDetails.createFlockDetails(
            conn, 1, dateThree, 15
        )

        val result = DepletedCount.getCumulativeDepletedCount(
            conn, 1
        )
        Assertions.assertEquals(15, result)
    }
}