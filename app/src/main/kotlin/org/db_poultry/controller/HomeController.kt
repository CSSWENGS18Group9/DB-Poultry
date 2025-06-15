package org.db_poultry.controller

import org.db_poultry.util.SceneSwitcher
import org.db_poultry.util.GeneralUtil

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable

import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.StackPane
import javafx.scene.text.Text

import javafx.scene.image.ImageView

import java.net.URL
import java.util.ResourceBundle

class HomeController: Initializable {

    @FXML
    private lateinit var homeAnchorPane: AnchorPane

    @FXML
    private lateinit var homeStackPane: StackPane

    @FXML
    private lateinit var bgImageAnchorPane: AnchorPane

    @FXML
    private lateinit var homeBackgroundImageView: ImageView

    @FXML
    private lateinit var homeFlockBtn1: Button

    @FXML
    private lateinit var homeReturnLoginBtn: Button

    @FXML
    private lateinit var homeSuppliesBtn1: Button

    override fun initialize(url: URL?, rb: ResourceBundle?) {
        // homeBackgroundImageView.fitWidthProperty().bind(homeStackPane.widthProperty())
        // homeBackgroundImageView.fitHeightProperty().bind(homeStackPane.heightProperty())
    }

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
}