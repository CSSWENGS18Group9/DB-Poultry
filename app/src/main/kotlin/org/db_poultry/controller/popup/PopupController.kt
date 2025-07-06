package org.db_poultry.controller.popup

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text
import javafx.stage.Stage

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
        // TODO: Implement undo action logic @Dattebayo2505
        closePopup()
    }


}