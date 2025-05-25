package org.db_poultry.db

import org.db_poultry.errors.generateErrorMessage
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DBConnect(
    jdbcURL: String,
    username: String,
    password: String
) {
    val conn: Connection = DriverManager.getConnection(jdbcURL, username, password)

    init {
        try {
            println("Welcome to DBPoultry's DBMS")
            println("Connected to: $jdbcURL")
        } catch (e: SQLException) {
            generateErrorMessage(
                "Error at `init` in `DBConnect.kt`",
                "Could not connect to DBPoultry DBMS",
                "Check if the database is created in PostgresSQL", e
            )
        }
    }

    fun getConnection(): Connection? = connection
}
