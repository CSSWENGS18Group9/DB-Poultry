package org.db_poultry

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DBConnect(
    private val databaseName: String,
    private val jdbcURL: String,
    private val username: String,
    private val password: String
) {
    private var connection: Connection? = null

    init {
        try {
            connection = DriverManager.getConnection(jdbcURL, username, password)
            println("Welcome to DBPoultry's DBMS")
            println("Connected to: $jdbcURL")
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun getConnection(): Connection? {
        return connection
    }
}
