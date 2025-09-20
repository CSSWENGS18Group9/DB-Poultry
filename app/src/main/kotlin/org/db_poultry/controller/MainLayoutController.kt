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
import org.controlsfx.control.ToggleSwitch
import org.db_poultry.util.PopupUtil
import org.db_poultry.util.SceneSwitcher
import org.kordamp.ikonli.javafx.FontIcon
import kotlin.collections.remove

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

        // Setup dark mode toggle
        darkModeToggleSwitch.selectedProperty().addListener { _, _, darkModeEnabled ->
            toggleDarkMode(darkModeEnabled)
        }
    }

    private fun toggleDarkMode(darkMode: Boolean) {
        // Main containers

        // Get all labels in the scene
        val allLabels = findAllLabels(mainAnchorPane)

        // Get all FontIcons in the scene
        val allIcons = findAllFontIcons(mainAnchorPane)


        if (darkMode) {

            // Apply dark text styling to all labels
            allLabels.forEach { label ->
                if (!label.styleClass.contains("text-dark")) {
                    label.styleClass.add("text-dark")
                }
            }

            // Apply dark styling to all icons
            allIcons.forEach { icon ->
                icon.styleClass.add("icon-dark")
            }


            // Apply icon-dark class to icons
            suppliesFontIcon.styleClass.add("icon-dark")
            flockFontIcon.styleClass.add("icon-dark")

            backupLabel.styleClass.remove("highlight")
            backupLabel.styleClass.add("text-dark") // TODO: @Dattebayo25 will change pa yata idk
            backupLabel.styleClass.add("highlight-dark")

            mainAnchorPane.styleClass.add("main-pane-dark")
            contentAnchorPane.styleClass.add("right-pane-dark")

            // Update sidebar and navbar
            sidebarAnchorPane.styleClass.remove("sidebar")
            sidebarAnchorPane.styleClass.add("sidebar-dark")

            // Find the navbar AnchorPane
            val navbarPane = mainGridPane.children.find { it is AnchorPane && it.styleClass.contains("navbar") } as AnchorPane?
            navbarPane?.let {
                it.styleClass.remove("navbar")
                it.styleClass.add("navbar-dark")
            }

            // Update navigation elements
            suppliesNavFlowPane.styleClass.add("navbar-pane-dark")
            flockNavFlowPane.styleClass.add("navbar-pane-dark")
            updateSuppliesFlowPane.styleClass.add("sidebar-pane-dark")
            retrieveChickenFeedPane.styleClass.add("sidebar-pane-dark")
            flockSelectionFlowPane.styleClass.add("sidebar-pane-dark")

            logoutFlowPane.styleClass.add("sidebar-pane-dark")

            // Update active highlights if any
            if (suppliesLabel.styleClass.contains("underline-label")) {
                suppliesLabel.styleClass.remove("underline-label")
                suppliesLabel.styleClass.add("underline-label-dark")
            }
            if (flockLabel.styleClass.contains("underline-label")) {
                flockLabel.styleClass.remove("underline-label")
                flockLabel.styleClass.add("underline-label-dark")
            }

//            sideBarDateLabel.styleClass.remove("calendar")
            sideBarDateLabel.styleClass.add("calendar-dark")

            updateActiveElementsForDarkMode(true)
        } else {
            // Remove dark text styling
            allLabels.forEach { label ->
                label.styleClass.remove("text-dark")
            }

            allIcons.forEach { icon ->
                icon.styleClass.remove("icon-dark")
            }

            // Remove icon-dark class
            suppliesFontIcon.styleClass.remove("icon-dark")
            flockFontIcon.styleClass.remove("icon-dark")

            backupLabel.styleClass.remove("highlight-dark")
            backupLabel.styleClass.remove("text-dark")
            backupLabel.styleClass.remove("highlight-dark")

            // Restore light mode
            mainAnchorPane.styleClass.remove("main-pane-dark")
            contentAnchorPane.styleClass.remove("right-pane-dark")

            // Update sidebar and navbar
            sidebarAnchorPane.styleClass.remove("sidebar-dark")
            sidebarAnchorPane.styleClass.add("sidebar")

            val navbarPane = mainGridPane.children.find { it is AnchorPane && it.styleClass.contains("navbar-dark") } as AnchorPane?
            navbarPane?.let {
                it.styleClass.remove("navbar-dark")
                it.styleClass.add("navbar")
            }

            // Update navigation elements
            suppliesNavFlowPane.styleClass.remove("navbar-pane-dark")
            flockNavFlowPane.styleClass.remove("navbar-pane-dark")
            updateSuppliesFlowPane.styleClass.remove("sidebar-pane-dark")
            retrieveChickenFeedPane.styleClass.remove("sidebar-pane-dark")
            flockSelectionFlowPane.styleClass.remove("sidebar-pane-dark")

            logoutFlowPane.styleClass.remove("sidebar-pane-dark")

            // Update active highlights if any
            if (suppliesLabel.styleClass.contains("underline-label-dark")) {
                suppliesLabel.styleClass.remove("underline-label-dark")
                suppliesLabel.styleClass.add("underline-label")
            }
            if (flockLabel.styleClass.contains("underline-label-dark")) {
                flockLabel.styleClass.remove("underline-label-dark")
                flockLabel.styleClass.add("underline-label")
            }

            sideBarDateLabel.styleClass.remove("calendar-dark")

            updateActiveElementsForDarkMode(false)
        }
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

    private fun findAllLabels(parent: javafx.scene.Parent): List<Label> {
        val result = mutableListOf<Label>()
        for (node in parent.childrenUnmodifiable) {
            if (node is Label) {
                result.add(node)
            }
            if (node is javafx.scene.Parent) {
                result.addAll(findAllLabels(node))
            }
        }
        return result
    }

    private fun findAllFontIcons(parent: javafx.scene.Parent): List<FontIcon> {
        val result = mutableListOf<FontIcon>()
        for (node in parent.childrenUnmodifiable) {
            if (node is FontIcon) {
                result.add(node)
            }
            if (node is javafx.scene.Parent) {
                result.addAll(findAllFontIcons(node))
            }
        }
        return result
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
        }
    }

    private fun clearAllHighlights() {
        suppliesLabel.styleClass.remove("underline-label")
        flockLabel.styleClass.remove("underline-label")

        // Clear sidebar highlights
        updateSuppliesFlowPane.styleClass.remove("sidebar-pane-active")
        retrieveChickenFeedPane.styleClass.remove("sidebar-pane-active")
        flockSelectionFlowPane.styleClass.remove("sidebar-pane-active")
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