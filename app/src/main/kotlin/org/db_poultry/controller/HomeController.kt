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

    override fun initialize(url: URL?, rb: ResourceBundle?) {

    }

    @FXML
    fun switchToFlock() {
        GeneralUtil.navigateToMainContent(homeAnchorPane, "/fxml/content_home_flock.fxml")
    }

    @FXML
    fun switchToSupplies() {
        GeneralUtil.navigateToMainContent(homeAnchorPane, "/fxml/content_home_supplies.fxml")

    }

}