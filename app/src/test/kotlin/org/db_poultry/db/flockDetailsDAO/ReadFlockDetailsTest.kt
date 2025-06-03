package org.db_poultry.db.flockDetailsDAO

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.db.flockDAO.CreateFlock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.Date

class ReadFlockDetailsTest {
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
    fun testReadFlockDetailsValidInput() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")


        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, 1, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, 1, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, 1, dateThree, 15)

        val result = ReadFlockDetails.getMostRecent(conn, dateFlock)


        Assertions.assertEquals(1, result.flockId)
        Assertions.assertEquals(dateThree, result.fdDate)
        Assertions.assertEquals(15, result.depletedCount)
    }

    @Test
    fun testReadFlockDetailsWithNoData() {
        val dateFlock = Date.valueOf("1000-01-01")

        val result = ReadFlockDetails.getMostRecent(conn, dateFlock)

        Assertions.assertNull(result)
    }
}