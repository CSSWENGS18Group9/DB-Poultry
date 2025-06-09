package org.db_poultry.controller

import org.db_poultry.Util.SceneSwitcher
import org.db_poultry.Util.GeneralUtil

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text

class SuppliesHomeController {

    @FXML
    private lateinit var homeAnchorPane: AnchorPane

    @FXML
    private lateinit var homeSuppliesBtn1: Button

    @FXML
    private lateinit var homeSuppliesBtn2: Button

    @FXML
    private lateinit var homeSuppliesBtn3: Button

    @FXML
    private lateinit var txtHomeMenu1: Text

    @FXML
    fun switchToMenu(event: ActionEvent) {
        GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_home.fxml")
    }

    @FXML
    fun switchToCreate(event: ActionEvent) {
        GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_create_supplies.fxml")
    }

}
