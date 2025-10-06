package org.db_poultry

import io.github.cdimascio.dotenv.Dotenv
import javafx.application.Application
import org.db_poultry.App.databaseName
import org.db_poultry.App.databasePass
import org.db_poultry.App.getDotEnv
import org.db_poultry.controller.DatabasePasswordNameController
import org.db_poultry.controller.MainFrame
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.db.initDBAndUser
import org.db_poultry.db.initTables
import org.db_poultry.errors.generateErrorMessage
import org.db_poultry.theLifesaver.Backup
import org.db_poultry.theLifesaver.Config.TL_loadConfig
import org.db_poultry.theLifesaver.ENV
import org.db_poultry.theLifesaver.TL.TL_firstOpen
import org.db_poultry.theLifesaver.TL.wipe
import org.db_poultry.theLifesaver.Variables
import java.nio.file.Files
import java.sql.Connection

object App {
    lateinit var databaseName: String
    lateinit var databasePass: String
    lateinit var databasePort: String

    fun getDotEnv(): Boolean {
        val envPath = Variables.getENVFilePath()
        if (!Files.exists(envPath)) { // if .env does not exist
            return false
        }

        try {
            val dotenv = Dotenv.configure()
                .directory(envPath.parent.toString()) // .db_poultry folder
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
                "Ensure .env exists in `\"Username\"/.db_poultry`.",
                e
            )
            return false
        }
    }

    fun fillDatabasePasswordName(inputPassword: String, inputName: String) {
        databasePass = inputPassword
        databaseName = inputName
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

    fun start(inputPassword: String, inputUsername: String) {
        if (!getDotEnv()) { // create missing .env file in .db_poultry
            println(".env not found. creating")
            ENV.makeENVfile() // create the .env file
            ENV.writeENVfile(inputPassword, inputUsername) // write contents with filled-in db name and port. password to be filled-in by user
            getDotEnv()
        }
    }

    fun connect() {
        val jdbcUrl = "jdbc:postgresql://localhost:$databasePort/$databaseName"

        println("connecting to $jdbcUrl")

        // Connect to the PostgresSQL DB
        DBConnect.init(jdbcUrl, databaseName, databasePass)
    }

    fun getConnection(): Connection? = DBConnect.getConnection()
}

// checks if the developers are the ones running the code
val __CLIENT_MODE: Boolean = true

// Do clean database. Should always be FALSE!
val __DO_WIPE: Boolean = false

fun main() {
    if (!getDotEnv()) { // if .env missing
        // FIXME: UI opens here, takes user input for password and name (use DatabasePasswordUsernameController)
        DatabasePasswordNameController().userPasswordName("testPW", "testDBNAME")
        println("inputs are $databaseName and $databasePass")
        println(".env missing")
    }

    App.start(App.databasePass, App.databaseName)

    var config: HashMap<String, String>? = null
    if (__CLIENT_MODE) {
        // Check if this is the first open
        config = TL_loadConfig()
        if (config == null)
            TL_firstOpen(App)
        else
            Backup.TL_checkLastBackupDate(config, App.databaseName, App.databasePass)
    }

    if (config == null) {
        initDBAndUser(databasePass, databaseName)
        println("Initialized DB and User with values $databaseName and $databasePass")
        App.connect()
        println("Connected to $databaseName")
        initTables(App.getConnection(), App.databaseName)
        println("Initialized tables")
    }

    println("passed initialization")
    App.connect()
    println("Connected to $databaseName")

    // Open MainFrame (index GUI)
    App.openMainFrame()

    // ==================================================
    // Keep this here but remove before shipping or every release
    // ==================================================
    if (__DO_WIPE) {
        App.getConnection()?.close()
        cleanTables(App.getConnection(), App.databaseName)
        wipe(App.databaseName)
    }
}