package org.db_poultry.controller.flock.popup

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.stage.Stage
import org.db_poultry.db.DBConnect
import org.db_poultry.db.flockDAO.CreateFlock
import org.db_poultry.util.GeneralUtil
import org.db_poultry.util.PopupUtil
import org.db_poultry.util.undoSingleton
import org.db_poultry.util.undoTypes
import java.sql.Date

class FlockCreateNewController {
    // private var jdbcURL: String

    @FXML
    private lateinit var createNewFlockAnchorPane: AnchorPane

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

        if (CreateFlock.createFlock(DBConnect.getConnection(), startCount, date) != null) {
            undoSingleton.setUndoMode(undoTypes.doUndoFlock)
            PopupUtil.showPopup("success", "Flock created successfully!")
            println("Successfully created Flock.")
        } else {
            PopupUtil.showPopup("error", "Flock creation error, retry again.")
            println("Failed to create Flock.")
        }

        closePopup()
        refreshFlockGrid()
    }

    @FXML
    fun closePopup() {
        val stage = createNewFlockDatePicker.scene.window as Stage
        stage.close()
    }

    private fun refreshFlockGrid() {
        // Get the main stage and find the FlockGridHomeController
        val mainStage = javafx.application.Platform.runLater {
            // Navigate back to refresh the grid
            GeneralUtil.navigateToMainContent(null, "/fxml/content_view_flock.fxml")
        }
    }


}