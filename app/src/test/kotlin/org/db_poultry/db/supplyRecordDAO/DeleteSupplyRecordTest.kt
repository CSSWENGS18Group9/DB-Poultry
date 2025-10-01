package org.db_poultry.db.supplyRecordDAO

import org.db_poultry.db.DBConnect
import org.db_poultry.db.initDBAndUser
import org.db_poultry.db.initTables
import org.db_poultry.db.cleanAndInitTables
import org.db_poultry.db.supplyTypeDAO.CreateSupplyType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.sql.Connection
import java.sql.Date

/*
Check if:
working
will work if the latest thing was a month ago
will work if there is no data
 */

class DeleteSupplyRecordTest {
    private val jdbcURL = "jdbc:postgresql://localhost:5432/db_poultry_test"
    private val conn: Connection

    init {
        initDBAndUser()

        DBConnect.init(jdbcURL, "db_poultry_test", "db_poultry_test")
        conn = DBConnect.getConnection()!!

        initTables(conn)
    }

    @Test
    fun testDeleteRecordWithData() {
        val date = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(
            conn,
            "Test_1",
            "kg",
            "src/main/resources/img/supply-img/Apog.png",
            "src/main/resources/img/supply-img/default.png"
        )
        CreateSupplyType.createSupplyType(
            conn,
            "Test_2",
            "kg",
            "src/main/resources/img/supply-img/Apog.png",
            "src/main/resources/img/supply-img/default.png"
        )


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false,
            BigDecimal("50.00")
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false,
            BigDecimal("50.00")
        )

        val result = DeleteSupplyRecord.undoCreateSupplyRecord(conn)

        assertEquals("DELETE FROM Supply_Record ORDER BY Supply_ID DESC LIMIT 1", result)
        assertNull(ReadSupplyRecord.getOneByDateAndName(conn, date, "Test_2"))
        cleanAndInitTables(conn)
    }

    @Test
    fun testDeleteRecordRecentDataOldDate() {
        val dateNew = Date.valueOf("2025-01-03")
        val dateOld = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(
            conn,
            "Test_1",
            "kg",
            "src/main/resources/img/supply-img/Apog.png",
            "src/main/resources/img/supply-img/default.png"
        )
        CreateSupplyType.createSupplyType(
            conn,
            "Test_2",
            "kg",
            "src/main/resources/img/supply-img/Apog.png",
            "src/main/resources/img/supply-img/default.png"
        )


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            dateNew,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false,
            BigDecimal("50.00")
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            dateOld,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false,
            BigDecimal("50.00")
        )

        val result = DeleteSupplyRecord.undoCreateSupplyRecord(conn)

        assertEquals("DELETE FROM Supply_Record ORDER BY Supply_ID DESC LIMIT 1", result)
        assertNull(ReadSupplyRecord.getOneByDateAndName(conn, dateOld, "Test_2"))
    }

    @Test
    fun testDeleteRecordWithNoData() {
        val date = Date.valueOf("2025-01-02")
        val result = DeleteSupplyRecord.undoCreateSupplyRecord(conn)
        assertNull(result)
        assertNull(ReadSupplyRecord.getOneByDateAndName(conn, date, "Test_2"))
    }

    @Test
    fun testDeleteRecordWithErrorInInput(){
        val date = Date.valueOf("2025-01-03")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false,
            BigDecimal("50.00")
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false,
            BigDecimal("-50.00")
        )

        val result = DeleteSupplyRecord.undoCreateSupplyRecord(conn)

        assertEquals("DELETE FROM Supply_Record ORDER BY Supply_ID DESC LIMIT 1", result)
        assertNull(ReadSupplyRecord.getOneByDateAndName(conn, date, "Test_1"))
    }
}