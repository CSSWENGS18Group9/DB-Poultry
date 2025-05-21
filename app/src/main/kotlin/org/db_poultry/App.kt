package org.db_poultry

import javafx.application.Application

import org.db_poultry.DB.DBConnect
import org.db_poultry.GUI.Sample

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

        println("Connected to database: $databaseName")

        javafx.application.Application.launch(Sample::class.java)
    }
}

fun main() {
    App().start()
}
