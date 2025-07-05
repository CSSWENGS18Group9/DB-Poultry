package org.db_poultry.controller

import javafx.event.ActionEvent
import org.db_poultry.util.GeneralUtil

import javafx.fxml.FXML
import javafx.fxml.Initializable

import java.util.ResourceBundle
import java.net.URL

import java.time.LocalDate

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.image.ImageView
import javafx.scene.layout.FlowPane
import org.db_poultry.util.SceneSwitcher
import org.kordamp.ikonli.javafx.FontIcon

class MainLayoutController : Initializable {

    @FXML
    private lateinit var mainAnchorPane: AnchorPane

    @FXML
    private lateinit var contentAnchorPane: AnchorPane

    @FXML
    private lateinit var sideBarImageView: ImageView

    @FXML
    private lateinit var sideBarDateLabel: Label
    
    @FXML
    private lateinit var homeFontIcon: FontIcon

    @FXML
    private lateinit var suppliesFontIcon: FontIcon

    @FXML
    private lateinit var flockFontIcon: FontIcon

    @FXML
    private lateinit var homeFlowPane: FlowPane

    @FXML
    private lateinit var suppliesFlowPane: FlowPane

    @FXML
    private lateinit var flockFlowPane: FlowPane

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {

        val today = LocalDate.now()
        sideBarDateLabel.text = GeneralUtil.formatDatePretty(today)
//        sideBarDateLabel.text = "September 30, 2023" // For testing purposes, set the longest fixed date

        GeneralUtil.initializeFontSizeManager(mainAnchorPane)
        GeneralUtil.resizeImageViewToFit(mainAnchorPane, sideBarImageView)
        GeneralUtil.initializeIconSizeManager(mainAnchorPane, homeFontIcon)
        GeneralUtil.initializeIconSizeManager(mainAnchorPane, suppliesFontIcon)
        GeneralUtil.initializeIconSizeManager(mainAnchorPane, flockFontIcon)

        GeneralUtil.registerSectionChangeCallback { section ->
            updateSidebarHighlight(section)
        }

        GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_home.fxml")
    }

    private fun updateSidebarHighlight(section: String) {
        // Remove highlight from all panes
        clearAllHighlights()

        // Add highlight to current section
        when (section) {
            "HOME" -> homeFlowPane.styleClass.add("sidebar-pane-active")
            "SUPPLIES" -> suppliesFlowPane.styleClass.add("sidebar-pane-active")
            "FLOCK" -> flockFlowPane.styleClass.add("sidebar-pane-active")
        }
    }

    private fun clearAllHighlights() {
        homeFlowPane.styleClass.remove("sidebar-pane-active")
        suppliesFlowPane.styleClass.remove("sidebar-pane-active")
        flockFlowPane.styleClass.remove("sidebar-pane-active")
    }

    @FXML
    fun switchToLogin() {
        println("Switching to login")
        SceneSwitcher.switchTo("/fxml/login.fxml")
    }

    @FXML
    private fun navigateToHome() {
        GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_home.fxml")
    }
    
    @FXML
    private fun navigateToSupplies() {
        // GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_home_supplies.fxml")
        GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_home_supplies.fxml")
    }
    
    @FXML
    private fun navigateToFlock() {
        GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_home_flock.fxml")
    }
}