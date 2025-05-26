package org.db_poultry.db.flock_detailsDAO

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.flockDAO.CreateFlock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.sql.Date

class GetCumulativeDepletedCountTest {

    private var jdbcURL: String
    private var conn: DBConnect

    init {
        val app = App()

        app.getDotEnv()

        jdbcURL = "jdbc:postgresql://localhost:${app.databasePort}/${app.databaseName}"
        conn = DBConnect(jdbcURL, app.databaseName, app.databasePass)
    }

    @Test
    fun testGetCumulativeDepletedCount() {
        val dateOne = Date.valueOf("1000-05-01")
        val dateTwo = Date.valueOf("1000-06-01")
        val dateThree = Date.valueOf("1000-07-01")

        CreateFlockDetails.createFlockDetails(conn.getConnection(), 2, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn.getConnection(), 2, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn.getConnection(), 2, dateThree, 15)

        val result = GetCumulativeDepletedCount.cumulativeDepletedCount(conn.getConnection(), 2)
        assertEquals(30, result)
    }
}