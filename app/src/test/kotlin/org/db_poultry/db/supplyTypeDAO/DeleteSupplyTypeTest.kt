package org.db_poultry.db.supplyTypeDAO

import org.db_poultry.db.DBConnect
import org.db_poultry.db.initDBAndUser
import org.db_poultry.db.initTables
import org.db_poultry.db.cleanAndInitTables
import org.db_poultry.db.supplyRecordDAO.CreateSupplyRecord
import org.db_poultry.db.supplyRecordDAO.ReadSupplyRecord
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.sql.Connection
import java.sql.Date

class DeleteSupplyTypeTest {
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
    fun testDeleteTypeWithDataOne() {
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

        val result = DeleteSupplyType.undoCreateSupplyType(conn)

        assertEquals("DELETE FROM Supply_Type ORDER BY Supply_Type_ID DESC LIMIT 1", result)
        assertNull(ReadSupplyRecord.getOneByDateAndName(conn, date, "Test_2"))
        cleanAndInitTables(conn, name)
    }

    @Test
    fun testDeleteTypeWithDataTwo() {
        val dateOne = Date.valueOf("2025-01-03")
        val dateTwo = Date.valueOf("2025-01-02")

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
            dateOne,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false,
            BigDecimal("50.00")
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            dateTwo,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false,
            BigDecimal("50.00")
        )

        val result = DeleteSupplyType.undoCreateSupplyType(conn)

        assertEquals("DELETE FROM Supply_Type ORDER BY Supply_Type_ID DESC LIMIT 1", result)
        assertNull(ReadSupplyRecord.getOneByDateAndName(conn, dateTwo, "Test_2"))
        cleanAndInitTables(conn, name)
    }
}
