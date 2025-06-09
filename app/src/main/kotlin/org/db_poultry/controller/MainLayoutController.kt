package org.db_poultry.controller

import org.db_poultry.util.GeneralUtil

import javafx.fxml.FXML
import javafx.fxml.Initializable

import java.util.ResourceBundle
import java.time.LocalDate
import java.net.URL

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane

class MainLayoutController : Initializable {

    @FXML
    private lateinit var contentAnchorPane: AnchorPane

    @FXML
    private lateinit var mainSplitPane: SplitPane

    @FXML
    private lateinit var mainGridPane: GridPane

    @FXML
    private lateinit var sidebarHomeBtn: Button

    @FXML
    private lateinit var sidebarCreateBtn: Button

    @FXML
    private lateinit var sidebarViewBtn: Button

    @FXML
    private lateinit var sidebarGenerateBtn: Button

    @FXML
    private lateinit var sideBarDateLabel: Label
    
    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        // Set today's date
        val today = LocalDate.now()
        sideBarDateLabel.text = today.toString()

        GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_home.fxml")
    }
    
    @FXML
    private fun navigateToHome() {
        GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_home.fxml")
    }
    
    @FXML
    private fun navigateToCreate() {
        GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_create.fxml")
    }
    
    @FXML
    private fun navigateToView() {
        GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_view.fxml")
    }
}