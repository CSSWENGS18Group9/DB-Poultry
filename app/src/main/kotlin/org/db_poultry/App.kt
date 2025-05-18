package org.db_poultry

class App {
    private val databaseName = "postgres"
    private val jdbcURL = "jdbc:postgresql://localhost:5432/$databaseName"
    private val username = "postgres"
    private val password = "password" // make sure this matches your actual DB password

    fun start() {
        DBConnect(
            databaseName,
            jdbcURL,
            username,
            password
        )
    }
}

fun main() {
    App().start()
}
