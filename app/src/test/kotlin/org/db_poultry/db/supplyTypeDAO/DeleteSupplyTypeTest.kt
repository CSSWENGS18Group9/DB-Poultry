package org.db_poultry.db.supplyTypeDAO

import org.db_poultry.db.supplyRecordDAO.*
import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.sql.Connection
import java.sql.Date

class DeleteSupplyTypeTest {
    private var jdbcURL: String
    private var conn: Connection

    init {
        App.getDotEnv()
        jdbcURL = "jdbc:postgresql://localhost:${App.databasePort}/${App.databaseName}"
        DBConnect.init(jdbcURL, App.databaseName, App.databasePass)
        conn = DBConnect.getConnection()!!
        cleanTables(conn)
    }

    @Test
    fun testDeleteTypeWithDataOne() {
        val date = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")

        val result = DeleteSupplyType.undoCreateSupplyType(conn)

        assertEquals("DELETE FROM Supply_Type ORDER BY Supply_Type_ID DESC LIMIT 1", result)
        assertNull(ReadSupplyRecord.getOneByDateAndName(conn, date, "Test_2"))
    }

    @Test
    fun testDeleteTypeWithDataTwo() {
        val dateOne = Date.valueOf("2025-01-03")
        val dateTwo = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            dateOne,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            dateTwo,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )

        val result = DeleteSupplyType.undoCreateSupplyType(conn)

        assertEquals("DELETE FROM Supply_Type ORDER BY Supply_Type_ID DESC LIMIT 1", result)
        assertNull(ReadSupplyRecord.getOneByDateAndName(conn, dateTwo, "Test_2"))
    }

}
