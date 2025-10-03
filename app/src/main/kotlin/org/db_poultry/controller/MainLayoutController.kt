package org.db_poultry.controller

import org.db_poultry.util.GeneralUtil
import org.db_poultry.util.GUIUtil

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Parent

import java.util.ResourceBundle
import java.net.URL

import java.time.LocalDate

import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.scene.image.ImageView
import javafx.scene.layout.FlowPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import org.controlsfx.control.ToggleSwitch
import org.db_poultry.util.PopupUtil
import org.db_poultry.util.SceneSwitcher
import org.kordamp.ikonli.javafx.FontIcon

class MainLayoutController : Initializable {

    @FXML
    private lateinit var mainAnchorPane: AnchorPane

    @FXML
    private lateinit var mainGridPane: GridPane

    @FXML
    private lateinit var contentAnchorPane: AnchorPane

    @FXML
    private lateinit var sidebarAnchorPane: AnchorPane

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
    private lateinit var logoutFlowPane: FlowPane

    @FXML
    private lateinit var backupLabel: Label

    @FXML
    private lateinit var darkModeToggleSwitch: ToggleSwitch

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

        // Setup dark mode toggle - initialize with persisted state from GUIUtil
        darkModeToggleSwitch.isSelected = GUIUtil.getDarkMode()

        // Apply the persisted dark mode state to the main layout immediately
        if (GUIUtil.getDarkMode()) {
            GUIUtil.applyDarkMode(mainAnchorPane, true)
            updateActiveElementsForDarkMode(true)
        }

        // Listen for toggle changes and update GUIUtil
        darkModeToggleSwitch.selectedProperty().addListener { _, _, darkModeEnabled ->
            GUIUtil.applyDarkMode(mainAnchorPane, darkModeEnabled)
            updateActiveElementsForDarkMode(darkModeEnabled)

            // Replace sidebar image based on dark mode
            val imagePath = if (darkModeEnabled) "/img/CSSWENG_DB Poultry Logo Dark.png" else "/img/CSSWENG_DB Poultry Logo.png"
            sideBarImageView.image = Image(javaClass.getResourceAsStream(imagePath))

            // Apply to currently loaded content if it exists
            if (contentAnchorPane.children.isNotEmpty()) {
                val currentContent = contentAnchorPane.children[0]
                GUIUtil.applyDarkMode(currentContent as Parent, darkModeEnabled)
            }
        }

        // Set initial sidebar image based on persisted dark mode state
        val initialImagePath = if (GUIUtil.getDarkMode()) "/img/CSSWENG_DB Poultry Logo Dark.png" else "/img/CSSWENG_DB Poultry Logo.png"
        sideBarImageView.image = Image(javaClass.getResourceAsStream(initialImagePath))
    }

    private fun updateActiveElementsForDarkMode(darkMode: Boolean) {
        // Convert active elements between light/dark mode
        val elementsToCheck = listOf(updateSuppliesFlowPane, retrieveChickenFeedPane, flockSelectionFlowPane)

        for (element in elementsToCheck) {
            if (darkMode && element.styleClass.contains("sidebar-pane-active")) {
                element.styleClass.remove("sidebar-pane-active")
                element.styleClass.add("sidebar-pane-active-dark")
            } else if (!darkMode && element.styleClass.contains("sidebar-pane-active-dark")) {
                element.styleClass.remove("sidebar-pane-active-dark")
                element.styleClass.add("sidebar-pane-active")
            }
        }
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
                if (GUIUtil.getDarkMode()) {
                    suppliesLabel.styleClass.add("underline-label-dark")
                } else {
                    suppliesLabel.styleClass.add("underline-label")
                }
                supplyGridPane.isVisible = true
                flockGridPane.isVisible = false
                supplyGridPane.isDisable = false
                flockGridPane.isDisable = true
            }
            "FLOCK" -> {
                if (GUIUtil.getDarkMode()) {
                    flockLabel.styleClass.add("underline-label-dark")
                } else {
                    flockLabel.styleClass.add("underline-label")
                }
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
            "SUPPLIES_UPDATE" -> {
                if (GUIUtil.getDarkMode()) {
                    updateSuppliesFlowPane.styleClass.add("sidebar-pane-active-dark")
                } else {
                    updateSuppliesFlowPane.styleClass.add("sidebar-pane-active")
                }
            }
            "SUPPLIES_RETRIEVE" -> {
                if (GUIUtil.getDarkMode()) {
                    retrieveChickenFeedPane.styleClass.add("sidebar-pane-active-dark")
                } else {
                    retrieveChickenFeedPane.styleClass.add("sidebar-pane-active")
                }
            }
            "FLOCK_SELECT" -> {
                if (GUIUtil.getDarkMode()) {
                    flockSelectionFlowPane.styleClass.add("sidebar-pane-active-dark")
                } else {
                    flockSelectionFlowPane.styleClass.add("sidebar-pane-active")
                }
            }
        }
    }

    private fun clearAllHighlights() {
        suppliesLabel.styleClass.remove("underline-label")
        suppliesLabel.styleClass.remove("underline-label-dark")
        flockLabel.styleClass.remove("underline-label")
        flockLabel.styleClass.remove("underline-label-dark")

        // Clear sidebar highlights
        updateSuppliesFlowPane.styleClass.remove("sidebar-pane-active")
        updateSuppliesFlowPane.styleClass.remove("sidebar-pane-active-dark")
        retrieveChickenFeedPane.styleClass.remove("sidebar-pane-active")
        retrieveChickenFeedPane.styleClass.remove("sidebar-pane-active-dark")
        flockSelectionFlowPane.styleClass.remove("sidebar-pane-active")
        flockSelectionFlowPane.styleClass.remove("sidebar-pane-active-dark")
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

    @FXML
    fun navigateToBackup() {
        PopupUtil.showContentPopup("/fxml/popup/backup/popup_backup_restore.fxml")
    }
}