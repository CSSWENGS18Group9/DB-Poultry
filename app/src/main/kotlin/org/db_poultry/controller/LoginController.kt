package org.db_poultry.controller

import org.db_poultry.util.SceneSwitcher
import org.db_poultry.util.GeneralUtil

import javafx.fxml.Initializable
import java.net.URL
import java.util.ResourceBundle

import javafx.fxml.FXML
import javafx.event.ActionEvent

import javafx.scene.layout.AnchorPane
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField

class LoginController: Initializable {

    @FXML
    private lateinit var loginAnchorPane: AnchorPane

    @FXML
    private lateinit var loginTitleLbl: Label

    @FXML
    private lateinit var passwordTextField: TextField

    @FXML
    private lateinit var usernameTextField: TextField

    @FXML
    private lateinit var signInBtn: Button
    
    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        GeneralUtil.initializeFontSizeManager(loginAnchorPane)
    }

    @FXML
    fun switchToHome(event: ActionEvent) {
        println("Switching to home view")
        SceneSwitcher.switchTo("/fxml/main_layout.fxml")
    }

}
