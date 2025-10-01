package org.db_poultry.db

import org.db_poultry.errors.generateErrorMessage
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DBConnect {
    private var connection: Connection? = null

    fun init(jdbcURL: String, username: String, password: String) {
        try {
            connection = DriverManager.getConnection(jdbcURL, username, password)
            println("Connected to: $jdbcURL")
        } catch (e: SQLException) {
            generateErrorMessage(
                "Error at `init` in `DBConnect.kt`",
                "Could not connect to database",
                "Check if the database is created in PostgresSQL", e
            )
        }
    }

    fun getConnection(): Connection? = connection
}