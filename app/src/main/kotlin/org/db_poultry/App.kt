package org.db_poultry

import io.github.cdimascio.dotenv.Dotenv
import javafx.application.Application
import org.db_poultry.controller.MainFrame
import org.db_poultry.db.DBConnect
import org.db_poultry.db.supplyTypeDAO.CreateSupplyType
import org.db_poultry.db.supplyTypeDAO.ReadSupplyType
import org.db_poultry.errors.generateErrorMessage
import org.db_poultry.theLifesaver.TL.*
import java.sql.Connection

class App {
    lateinit var databaseName: String
    lateinit var databasePass: String
    lateinit var databasePort: String

    fun getDotEnv(): Boolean {
        try {
            val dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load()

            databaseName = (dotenv["DATABASE_NAME"] ?: "Missing DATABASE_NAME")
            databasePass = (dotenv["DATABASE_PASS"] ?: "Missing DATABASE_PASS")
            databasePort = (dotenv["DATABASE_PORT"] ?: "Missing DATABASE_PORT")

            println("database name: $databaseName")
            println("database pass: $databasePass")
            println("database port: $databasePort")

            return true
        } catch (e: Exception) {
            generateErrorMessage(
                "Error at `getDotEnv()` in `App.kt`",
                "Failed to load environment variables",
                "In local dev, ensure .env exists in `app/src/main/resources`.",
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

    // theLifesaver (backup stuff)
    TL_firstOpen(app.getConnection())
    TL_checkLastBackupDate()

    // Open MainFrame (index GUI)
//    app.openMainFrame()
    CreateSupplyType.createSupplyType(app.getConnection(), "Hello", "Hello");
    CreateSupplyType.createSupplyType(app.getConnection(), "World", "World");
    CreateSupplyType.createSupplyType(app.getConnection(), "Hello", "Hello");

    val x = ReadSupplyType.getAllSupplyTypes(app.getConnection())
    for (t in x) println(t.name)

    // ==================================================
    // Keep this here but remove before shipping or every release
    // ==================================================
    TL_cleanAll()
}
