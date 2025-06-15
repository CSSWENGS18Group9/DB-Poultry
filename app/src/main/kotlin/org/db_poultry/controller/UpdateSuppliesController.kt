package org.db_poultry.controller

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.event.ActionEvent
import org.db_poultry.util.GeneralUtil

class UpdateSuppliesController {

    @FXML
    private lateinit var homeAnchorPane: AnchorPane

    @FXML
    private lateinit var txtHomeMenu1: Text

    @FXML
    private lateinit var updateSuppliesBtn1: Button

    @FXML
    private lateinit var updateSuppliesBtn2: Button

    @FXML
    fun switchToRetrieve(event: ActionEvent) {
        GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_update_supplies_retrieve.fxml")
    }

    @FXML
    fun switchToAddDelete(event: ActionEvent) {
        GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_update_supplies_add_delete.fxml")
    }

}
