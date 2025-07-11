package org.db_poultry.db.supplyTypeDAO

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.sql.Connection


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
    
    // - getAllSupplyTypes should return an empty list if no supply types exist.
    @Test
    fun testGetAllSupplyTypesEmpty() {
        val result = ReadSupplyType.getAllSupplyTypes(conn)
        assertEquals(emptyList<String>(), result)
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
        CreateSupplyType.createSupplyType(conn, "feed", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png\n")

        val result = ReadSupplyType.getSupplyTypeById(conn, 1)
        assertEquals("feed", result?.name)
        assertEquals("kg", result?.unit)
        assertEquals("src/main/resources/img/supply-img/Apog.png", result?.imagePath)
    }

    // - getSupplyTypeByName should return the correct supply type if the name exists.
    @Test
    fun testGetSupplyTypeByNameExisting() {
        CreateSupplyType.createSupplyType(conn, "water", "liter", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png\n")
        val result = ReadSupplyType.getSupplyTypeByName(conn, "water")
        assertEquals("water", result?.name)
        assertEquals("liter", result?.unit)
        assertEquals("src/main/resources/img/supply-img/Apog.png", result?.imagePath)

    }

    // - getSupplyTypeById should return null if the Name is empty
    @Test
    fun testGetSupplyTypeByIdEmptyName() {
        CreateSupplyType.createSupplyType(conn, "", "liter", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png\n")
        val result = ReadSupplyType.getSupplyTypeById(conn, 1)
        assertNull(result)
    }

    // - getSupplyTypeById should return null if the ID is empty
    @Test
    fun testGetSupplyTypeByIdEmptyUnit() {
        CreateSupplyType.createSupplyType(conn, "water", "", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png\n")
        val result = ReadSupplyType.getSupplyTypeById(conn, 1)
        assertNull(result)
    }
}