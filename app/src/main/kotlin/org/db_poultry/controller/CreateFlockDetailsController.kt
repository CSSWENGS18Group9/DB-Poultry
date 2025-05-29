package org.db_poultry.controller

import javafx.fxml.FXML
import javafx.scene.control.DatePicker
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import org.db_poultry.App
import org.db_poultry.controller.util.recordFlockDetails
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import java.sql.Date
import java.time.LocalDate
import javafx.scene.control.Button

class CreateFlockDetailsController {
    private var jdbcURL: String
    private var conn: DBConnect

    init {
        val app = App()

        app.getDotEnv()

        jdbcURL = "jdbc:postgresql://localhost:${app.databasePort}/${app.databaseName}"
        conn = DBConnect(jdbcURL, app.databaseName, app.databasePass)
        cleanTables(conn.getConnection())
    }

    val connection = conn.getConnection()

    @FXML
    private lateinit var anchorPane: AnchorPane

    @FXML
    private lateinit var datePicker: DatePicker

    @FXML
    private lateinit var shapeBg: Rectangle

    @FXML
    private lateinit var textBody1: Text

    @FXML
    private lateinit var textBody2: Text

    @FXML
    private lateinit var textField: TextField

    @FXML
    private lateinit var textHeader: Text

    @FXML
    private lateinit var depleteCountCFDtextField: TextField

    @FXML
    private lateinit var cFDDatePicker: DatePicker

    @FXML
    private lateinit var btnConfirm: Button

    @FXML
    fun confirm() {
        // still need to pass connection. will leave alone for now

        val flockDate = Date.valueOf(cFDDatePicker.value)
        val depletedCount = depleteCountCFDtextField.text.toInt()

        println("Flock Date: $flockDate")
        println("Depleted Count: $depletedCount")


        val detailDate = Date.valueOf(LocalDate.now())

        val feedback = recordFlockDetails(connection, flockDate, detailDate, depletedCount)

        when (feedback) {
            1 -> println("Successfully created new flock")
            0, -1 -> println("Error creating new flock")
        }

    }

}
