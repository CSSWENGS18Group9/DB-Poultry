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

    private fun findMainContentPane(): AnchorPane? {
        // Traverse up the scene graph to find the main content pane (avoid nested FXML structures)
        var parent = homeAnchorPane.parent
        while (parent != null) {
            if (parent is AnchorPane && parent.id == "contentAnchorPane") {
                return parent
            }
            parent = parent.parent
        }
        return null
    }

    @FXML
    fun switchToFlock() {
        // Find the main content area by traversing up the scene graph
        val mainContentPane = findMainContentPane()
        if (mainContentPane != null) {
            GeneralUtil.loadContentView(mainContentPane, "/fxml/content_home_flock.fxml")
        }
    }

    @FXML
    fun switchToSupplies() {
        // Find the main content area by traversing up the scene graph
        val mainContentPane = findMainContentPane()
        if (mainContentPane != null) {
            GeneralUtil.loadContentView(mainContentPane, "/fxml/content_home_supplies.fxml")
        }
    }

}