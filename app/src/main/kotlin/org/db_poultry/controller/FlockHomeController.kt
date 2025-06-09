package org.db_poultry.controller

import org.db_poultry.Util.SceneSwitcher
import org.db_poultry.Util.GeneralUtil

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text

class FlockHomeController {

    @FXML
    private lateinit var homeAnchorPane: AnchorPane

    @FXML
    private lateinit var homeFlockBtn1: Button

    @FXML
    private lateinit var homeFlockBtn2: Button

    @FXML
    private lateinit var homeFlockBtn3: Button

    @FXML
    private lateinit var txtHomeMenu1: Text

    @FXML
    fun goToCreate(event: ActionEvent) {
        GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_create.fxml")
    }

    @FXML
    fun goToView(event: ActionEvent) {
        GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_view.fxml")
    }

    @FXML
    fun switchToMenu(event: ActionEvent) {
        GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_home.fxml")
    }

}
