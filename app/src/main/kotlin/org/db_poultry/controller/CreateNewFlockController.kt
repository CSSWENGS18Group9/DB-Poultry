package org.db_poultry.controller

import org.db_poultry.db.flockDAO.ReadFlock
import org.db_poultry.App
import org.db_poultry.controller.util.recordFlock
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables

import javafx.fxml.FXML
import javafx.scene.control.DatePicker
import javafx.scene.control.TextField
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.scene.layout.AnchorPane
import javafx.scene.control.Button
import java.sql.Date

class CreateNewFlockController {
    // private var jdbcURL: String    

    @FXML
    private lateinit var anchorPane: AnchorPane

    @FXML
    private lateinit var createNewFlockDatePicker: DatePicker

    @FXML
    private lateinit var shapeBg: Rectangle

    @FXML
    private lateinit var textBody1: Text

    @FXML
    private lateinit var textBody2: Text

    @FXML
    private lateinit var createNewFlockTextField: TextField

    @FXML
    private lateinit var textHeader: Text

    @FXML
    private lateinit var btnConfirm: Button

    @FXML
    fun confirm() {
        // still need to pass connection. will leave alone for now

        val startCount = createNewFlockTextField.text.toInt()
        val date = Date.valueOf(createNewFlockDatePicker.value)

        println("\nStart Count: $startCount")
        println("Date: $date")

        val feedback = recordFlock(DBConnect.getConnection(), startCount, date)
       
        when (feedback) {
            1 -> println("Successfully created new flock")
            0, -1 -> println("Error creating new flock")
        }

    }

}
