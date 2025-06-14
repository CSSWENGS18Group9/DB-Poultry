package org.db_poultry

import io.github.cdimascio.dotenv.Dotenv
import javafx.application.Application
import org.db_poultry.controller.CreateNewFlockController
import org.db_poultry.controller.CreateFlockDetailsController
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.db.flockDetailsDAO.ReadFlockDetails
import org.db_poultry.errors.generateErrorMessage
import org.db_poultry.controller.MainFrame
import java.sql.Connection
import java.sql.Date

class App {
    lateinit var databaseName: String
    lateinit var databasePass: String
    lateinit var databasePort: String

    fun getDotEnv(): Boolean {
        try {
            val dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .directory("./app")
                .load()

            databaseName = dotenv["DATABASE_NAME"] ?: System.getenv("DB_NAME") ?: error("Missing DATABASE_NAME")
            databasePass = dotenv["DATABASE_PASS"] ?: System.getenv("DB_PASS") ?: error("Missing DATABASE_PASS")
            databasePort = dotenv["DATABASE_PORT"] ?: System.getenv("DB_PORT") ?: error("Missing DATABASE_PORT")

            println("database name: $databaseName")
            println("database pass: $databasePass")
            println("database port: $databasePort")

            return true
        } catch (e: Exception) {
            generateErrorMessage(
                "Error at `getDotEnv()` in `App.kt`",
                "Failed to load environment variables",
                "In local dev, ensure .env exists in `app/`. In GitHub Actions, add secrets: `DB_NAME`, `DB_PASS`, `DB_PORT`.",
                e
            )
            return false
        }
    }


    fun openMainFrame(): Boolean {
        try {
            Application.launch(MainFrame::class.java)

            return true
        } catch (e: ClassNotFoundException) {
            generateErrorMessage(
                "Error at `openMainFrame()` in `App.kt`.",
                "Failed to open MainFrame GUI; class not found.",
                "Rename the `index` GUI to `MainFrame` and should be at the root folder of `Gui/`.", e
            )

            return false
        }
    }

    fun start() {
        if (!getDotEnv()) {
            generateErrorMessage(
                "Error at `start()` in `App.kt`.",
                "Dot env file has a missing variable.",
                "Check if the dot env file has DATABASE_NAME, DATABASE_PASS, and DATABASE_PORT"
            )

            return
        }

        val jdbcUrl = "jdbc:postgresql://localhost:$databasePort/$databaseName"

        // Connect to the PostgresSQL DB
        DBConnect.init(jdbcUrl, databaseName, databasePass)

    }

    fun getConnection(): Connection? = DBConnect.getConnection()
}

fun main() {
    val app = App()
    app.start()

    // Open MainFrame (index GUI)
    app.openMainFrame()


}
