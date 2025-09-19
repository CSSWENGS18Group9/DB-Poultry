package org.db_poultry.db.supplyTypeDAO

import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.db.supplyRecordDAO.CreateSupplyRecord
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.sql.Connection
import java.sql.Date


// Test class for ReadSupplyType
// This class tests the functionality of reading supply types from the database.
// Restrictions include:
// - getAllSupplyTypes should return an empty list if no supply types exist.
// - Reading a supply type with a non-existent ID should return null.
// - Reading a supply type with an existing ID should return the correct supply type.
// - getSupplyTypeByName should return null if the name does not exist.
// - getSupplyTypeByName should return the correct supply type if the name exists.
// - getSupplyTypeById should return null if the ID does not exist.
// - getSupplyTypeById should return the correct supply type if the ID exists.


class ReadSupplyTypeTest {
    private val jdbcURL = "jdbc:postgresql://localhost:5432/db_poultry_test"
    private val conn: Connection

    init {
        initDBAndUser()

        DBConnect.init(jdbcURL, "db_poultry_test", "db_poultry_test")
        conn = DBConnect.getConnection()!!

        initTables(conn)
    }

    // - getAllSupplyTypes should return an empty list if no supply types exist.
    @Test
    fun testGetAllSupplyTypesDefault() {
        val result = ReadSupplyType.getAllSupplyTypes(conn)
        assertEquals(12, result.size)
    }

    // - Reading a supply type with a non-existent ID should return null.
    @Test
    fun testGetSupplyTypeByIdNonExistent() {
        val result = ReadSupplyType.getSupplyTypeById(conn, 9999)
        assertNull(result)
    }

    // - getSupplyTypeByName should return null if the name does not exist.
    @Test
    fun testGetSupplyTypeByNameNonExistent() {
        val result = ReadSupplyType.getSupplyTypeByName(conn, "NonExistent")
        assertNull(result)
    }

    // - Reading a supply type with an existing ID should return the correct supply type.
    @Test
    fun testGetSupplyTypeByIdExisting() {
        CreateSupplyType.createSupplyType(
            conn,
            "test_1",
            "kg",
            "src/main/resources/img/supply-img/Apog.png",
            "src/main/resources/img/supply-img/default.png"
        )

        val result = ReadSupplyType.getSupplyTypeById(conn, 13)
        assertEquals("test_1", result?.name)
        assertEquals("kg", result?.unit)
        assertEquals("src/main/resources/img/supply-img/Apog.png", result?.imagePath)
    }

    // - getSupplyTypeByName should return the correct supply type if the name exists.
    @Test
    fun testGetSupplyTypeByNameExisting() {
        CreateSupplyType.createSupplyType(
            conn,
            "water",
            "liter",
            "src/main/resources/img/supply-img/Apog.png",
            "src/main/resources/img/supply-img/default.png"
        )
        val result = ReadSupplyType.getSupplyTypeByName(conn, "water")
        assertEquals("water", result?.name)
        assertEquals("liter", result?.unit)
        assertEquals("src/main/resources/img/supply-img/Apog.png", result?.imagePath)

    }

    // - getSupplyTypeById should return null if the Name is empty
    @Test
    fun testGetSupplyTypeByIdEmptyName() {
        CreateSupplyType.createSupplyType(
            conn,
            "",
            "liter",
            "src/main/resources/img/supply-img/Apog.png",
            "src/main/resources/img/supply-img/default.png"
        )
        val result = ReadSupplyType.getSupplyTypeById(conn, 13)
        assertNull(result)
    }

    // - getSupplyTypeById should return null if the ID is empty
    @Test
    fun testGetSupplyTypeByIdEmptyUnit() {
        CreateSupplyType.createSupplyType(
            conn,
            "temp_1",
            "",
            "src/main/resources/img/supply-img/Apog.png",
            "src/main/resources/img/supply-img/default.png"
        )
        val result = ReadSupplyType.getSupplyTypeById(conn, 13)
        assertNull(result)
    }

    @Test
    fun testGetSupplyTypeAscendingWithData() {
        val result = ReadSupplyType.getSupplyTypeAscending(conn)
        assertEquals("adulticide", result[0].name)
        assertEquals("apog", result[1].name)
    }

    @Test
    fun testGetSupplyTypeDescendingWithData() {
        CreateSupplyType.createSupplyType(
            conn,
            "test_1",
            "kg",
            "src/main/resources/img/supply-img/Apog.png",
            "src/main/resources/img/supply-img/default.png"
        )
        CreateSupplyType.createSupplyType(
            conn,
            "test_2",
            "liter",
            "src/main/resources/img/supply-img/Apog.png",
            "src/main/resources/img/supply-img/default.png"
        )
        val result = ReadSupplyType.getSupplyTypeDescending(conn)
        assertEquals("test_2", result[0].name)
        assertEquals("test_1", result[1].name)
    }

    @Test
    fun testGetSupplyTypeByLastUpdateWithData() {
        CreateSupplyType.createSupplyType(
            conn,
            "test_1",
            "kg",
            "src/main/resources/img/supply-img/Apog.png",
            "src/main/resources/img/supply-img/default.png"
        )
        CreateSupplyType.createSupplyType(
            conn,
            "test_2",
            "liter",
            "src/main/resources/img/supply-img/Apog.png",
            "src/main/resources/img/supply-img/default.png"
        )

        // a supply record for feed
        // so now, feed will go first and water second
        CreateSupplyRecord.createSupplyRecord(
            conn,
            14,
            Date.valueOf("2025-01-01"),
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )

        val result = ReadSupplyType.getSupplyTypeByLastUpdate(conn)
        assertEquals("test_2", result[0].name)
    }
}