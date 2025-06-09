package org.db_poultry.controller

import org.db_poultry.util.SceneSwitcher
import org.db_poultry.util.GeneralUtil

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text

class HomeController {

    @FXML
    private lateinit var homeReturnLoginBtn: Button

    @FXML
    private lateinit var homeCreateBtn: Button

    @FXML
    private lateinit var homeViewBtn: Button

    @FXML
    private lateinit var homeGenerateBtn: Button

    @FXML
    private lateinit var homeAnchorPane: AnchorPane

    @FXML
    private lateinit var txtHomeMenu: Text

    @FXML
    fun switchToLogin(event: ActionEvent) {
        println("Switching to login")
        SceneSwitcher.switchTo(homeReturnLoginBtn, "/fxml/login.fxml")
    }

    @FXML
    private fun navigateToHome() {
        GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_home.fxml")
    }
    
    @FXML
    private fun navigateToCreate() {
        GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_create.fxml")
    }
    
    @FXML
    private fun navigateToView() {
        GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_view.fxml")
    }

}