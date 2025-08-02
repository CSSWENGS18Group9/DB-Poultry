package org.db_poultry.controller

import org.db_poultry.util.GeneralUtil

import javafx.fxml.FXML
import javafx.fxml.Initializable

import java.util.ResourceBundle
import java.net.URL

import java.time.LocalDate

import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.image.ImageView
import javafx.scene.layout.FlowPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
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

    // NEWGEN

    @FXML
    private lateinit var mainStackPane: StackPane

    @FXML
    private lateinit var suppliesNavFlowPane: FlowPane

    @FXML
    private lateinit var flockNavFlowPane: FlowPane

    @FXML
    private lateinit var suppliesLabel: Label

    @FXML
    private lateinit var flockLabel: Label

    @FXML
    private lateinit var supplyGridPane: GridPane

    @FXML
    private lateinit var flockGridPane: GridPane

    @FXML
    private lateinit var updateSuppliesFlowPane: FlowPane

    @FXML
    private lateinit var retrieveChickenFeedPane: FlowPane

    @FXML
    private lateinit var flockSelectionFlowPane: FlowPane

    @FXML
    private lateinit var flockGenerateReportFlowPane: FlowPane


    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {

        GeneralUtil.setMainContentPane(contentAnchorPane)

        val today = LocalDate.now()
        sideBarDateLabel.text = GeneralUtil.formatDatePretty(today)
//        sideBarDateLabel.text = "September 30, 2023" // For testing purposes, set the longest fixed date

        GeneralUtil.initializeFontSizeManager(mainAnchorPane)
        GeneralUtil.resizeImageViewToFit(mainAnchorPane, sideBarImageView)
        GeneralUtil.initializeIconSizeManager(mainAnchorPane, suppliesFontIcon, 30.0)
        GeneralUtil.initializeIconSizeManager(mainAnchorPane, flockFontIcon, 30.0)

        GeneralUtil.registerSectionChangeCallback { section ->
            updateSidebarHighlight(section)
        }

        GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_home.fxml")
    }

    private fun updateSidebarHighlight(section: String) {
        clearAllHighlights()

        // Handle navbar highlighting (main sections only)
        val mainSection = when (section) {
            "SUPPLIES", "SUPPLIES_UPDATE", "SUPPLIES_RETRIEVE" -> "SUPPLIES"
            "FLOCK", "FLOCK_SELECT", "FLOCK_GENERATE_REPORT" -> "FLOCK"
            else -> section
        }

        // Update navbar
        when (mainSection) {
            "SUPPLIES" -> {
                suppliesLabel.styleClass.add("underline-label")
                supplyGridPane.isVisible = true
                flockGridPane.isVisible = false
                supplyGridPane.isDisable = false
                flockGridPane.isDisable = true
            }
            "FLOCK" -> {
                flockLabel.styleClass.add("underline-label")
                supplyGridPane.isVisible = false
                flockGridPane.isVisible = true
                flockGridPane.isDisable = false
                supplyGridPane.isDisable = true
            }
            else -> {
                // Hide both if not in SUPPLIES or FLOCK section
                supplyGridPane.isVisible = false
                flockGridPane.isVisible = false
                supplyGridPane.isDisable = true
                flockGridPane.isDisable = true
            }
        }

        // Update sidebar based on specific subsection
        when (section) {
            "SUPPLIES_UPDATE" -> updateSuppliesFlowPane.styleClass.add("sidebar-pane-active")
            "SUPPLIES_RETRIEVE" -> retrieveChickenFeedPane.styleClass.add("sidebar-pane-active")
            "FLOCK_SELECT" -> flockSelectionFlowPane.styleClass.add("sidebar-pane-active")
            "FLOCK_GENERATE_REPORT" -> flockGenerateReportFlowPane.styleClass.add("sidebar-pane-active")
        }
    }

    private fun clearAllHighlights() {
        suppliesLabel.styleClass.remove("underline-label")
        flockLabel.styleClass.remove("underline-label")

        // Clear sidebar highlights
        updateSuppliesFlowPane.styleClass.remove("sidebar-pane-active")
        retrieveChickenFeedPane.styleClass.remove("sidebar-pane-active")
        flockSelectionFlowPane.styleClass.remove("sidebar-pane-active")
        flockGenerateReportFlowPane.styleClass.remove("sidebar-pane-active")
    }

    @FXML
    fun switchToLogin() {
        println("Switching to login")
        SceneSwitcher.switchTo("/fxml/login.fxml")
    }

    @FXML
    fun navigateToHome() {
        GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_home.fxml")
    }

    @FXML
    fun navigateToUpdateSupplies() {
        GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_home_supplies_grid.fxml")
    }

    @FXML
    fun navigateToRetrieveChickenFeed() {
        GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_update_supplies_retrieve.fxml")
    }

    @FXML
    fun navigateToFlockSelection() {
        GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_home_flock_grid.fxml")
    }
}