package org.db_poultry.controller

import org.db_poultry.Util.SceneSwitcher
import org.db_poultry.Util.GeneralUtil

import javafx.event.ActionEvent
import javafx.fxml.FXML

import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text

class HomeController {

    @FXML
    private lateinit var homeAnchorPane: AnchorPane

    @FXML
    private lateinit var homeFlockBtn1: Button

    @FXML
    private lateinit var homeReturnLoginBtn: Button

    @FXML
    private lateinit var homeSuppliesBtn1: Button

    @FXML
    private lateinit var txtHomeMenu1: Text

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
    fun switchToFlock() {
        GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_home_flock.fxml")
    }

    @FXML 
    fun switchToSupplies() {
        GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_home_supplies.fxml")
    }

    // @FXML
    // private fun navigateToCreate() {
    //     GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_create.fxml")
    // }
    
    // @FXML
    // private fun navigateToView() {
    //     GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_view.fxml")
    // }

}