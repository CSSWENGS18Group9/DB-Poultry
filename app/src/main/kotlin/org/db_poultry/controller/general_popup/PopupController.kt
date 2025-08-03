package org.db_poultry.controller.general_popup

import org.db_poultry.db.DBConnect
import org.db_poultry.util.undoSingleton
import org.db_poultry.controller.NotificationController

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text
import javafx.stage.Stage
import org.db_poultry.errors.generateErrorMessage
import org.db_poultry.util.undoTypes

class PopupController {

    @FXML
    private var popupSuccessAnchorPane: AnchorPane? = null

    @FXML
    private var popupSuccessText: Text? = null

    @FXML
    private var closeSuccessButton: Button? = null

    @FXML
    private var popupErrorAnchorPane: AnchorPane? = null

    @FXML
    private var popupErrorText: Text? = null

    @FXML
    private var undoButton: Button? = null

    @FXML
    private var closeErrorButton: Button? = null

    @FXML
    fun closePopup() {
        val stage = popupSuccessAnchorPane?.scene?.window as? Stage
            ?: popupErrorAnchorPane?.scene?.window as? Stage
        stage?.close()
    }

    @FXML
    fun undoAction() {
        val connection = DBConnect.getConnection()

        if (connection != null) {

            if (undoSingleton.getIsFeedRetrieval()) {
                for (i in 0 until 4) {
                    undoSingleton.setUndoMode(undoTypes.doUndoSupplyRecord) // Set mode before each call
                    undoSingleton.undo(connection)
                }
            }
            undoSingleton.setIsFeedRetrieval(false)
            undoSingleton.undo(connection)


            NotificationController.showNotification()

        } else {
            generateErrorMessage(
                "Database Connection Error",
                "Failed to connect to the database. Please check your connection settings.",
                "Check your database connection settings and try again."
            )
        }

        closePopup()

    }


}