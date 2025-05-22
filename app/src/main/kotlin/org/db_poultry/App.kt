package org.db_poultry

import javafx.application.Application
import io.github.cdimascio.dotenv.Dotenv
import org.db_poultry.DB.DBConnect
import org.db_poultry.GUI.Sample

class App {
    private lateinit var databaseName: String
    private lateinit var databasePass: String
    private lateinit var databasePort: String

    private fun getDotEnv(): Boolean {
        try {
            val dotenv = Dotenv.load()

            databaseName = dotenv["DATABASE_NAME"] ?: error("Missing DATABASE_NAME")
            databasePass = dotenv["DATABASE_PASS"] ?: error("Missing DATABASE_PASS")
            databasePort = dotenv["DATABASE_PORT"] ?: error("Missing DATABASE_PORT")

            println("database name:$databaseName")
            println("database pass:$databasePass")
            println("database port:$databasePort")

            return true
        } catch (e: Exception) {
            println("Error at `getDotEnv() in `App.kt`")
            println("Failed to load environment variables not found.")
            e.printStackTrace()

            return false
        }

        return false
    }

    fun start() {
        if(!getDotEnv()){
            println("Error at `start()` in `App.kt`")
            println("Environment (dot env) has a missing variable.")
            return
        }

        val jdbcUrl = "jdbc:postgresql://localhost:$databasePort/$databaseName"

        DBConnect(
            databaseName,
            jdbcUrl,
            databaseName,
            databasePass
        )

    }
}

fun main() {
    App().start()
}
