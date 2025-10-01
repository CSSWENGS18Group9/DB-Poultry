package org.db_poultry.util

import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.FlowPane
import org.kordamp.ikonli.javafx.FontIcon
import org.db_poultry.theLifesaver.Variables
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

object GUIUtil {

    private const val DARK_MODE_KEY = "dark_mode"

    private var isDarkMode = false

    init {
        // Load dark mode setting from file on startup
        loadDarkModeFromFile()
    }

    fun getDarkMode(): Boolean {
        return isDarkMode
    }

    fun setDarkMode(darkMode: Boolean) {
        isDarkMode = darkMode
        // Save setting to file whenever it changes
        saveDarkModeToFile()
    }

    private fun loadDarkModeFromFile() {
        try {
            val dmConfigPath = Variables.getDMConfigPath()
            val configFile = File(dmConfigPath)

            // Ensure .db_poultry directory exists
            val appDir = File(Variables.getHomeDirectory(), Variables.getAppFolder())
            if (!appDir.exists()) {
                appDir.mkdirs()
            }

            if (configFile.exists()) {
                val properties = Properties()
                FileInputStream(configFile).use { input ->
                    properties.load(input)
                }
                isDarkMode = properties.getProperty(DARK_MODE_KEY, "false").toBoolean()
                println("Loaded dark mode setting: $isDarkMode")
            } else {
                // Create default config file
                isDarkMode = false
                saveDarkModeToFile()
                println("Created new dark mode config with default value: $isDarkMode")
            }
        } catch (e: Exception) {
            println("Could not load dark mode setting: ${e.message}")
            isDarkMode = false
        }
    }

    private fun saveDarkModeToFile() {
        try {
            val dmConfigPath = Variables.getDMConfigPath()
            val properties = Properties()

            val appDir = File(Variables.getHomeDirectory(), Variables.getAppFolder())
            if (!appDir.exists()) {
                appDir.mkdirs()
            }

            // Load existing properties first to preserve other settings (if any)
            val configFile = File(dmConfigPath)
            if (configFile.exists()) {
                FileInputStream(configFile).use { input ->
                    properties.load(input)
                }
            }

            // Update dark mode setting
            properties.setProperty(DARK_MODE_KEY, isDarkMode.toString())

            // Save properties
            FileOutputStream(configFile).use { output ->
                properties.store(output, "Dark Mode Configuration")
            }
            println("Saved dark mode setting to dm_config: $isDarkMode")
        } catch (e: Exception) {
            println("Could not save dark mode setting: ${e.message}")
        }
    }

    fun applyDarkMode(parent: Parent, darkMode: Boolean) {
        // Update the internal state and save it
        if (isDarkMode != darkMode) {
            setDarkMode(darkMode)
        }

        // Get all labels and icons in the scene
        val allLabels = findAllLabels(parent)
        val allIcons = findAllFontIcons(parent)

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

            // Apply dark mode to common container types
            applyDarkModeToContainers(parent, true)

        } else {
            // Remove dark text styling
            allLabels.forEach { label ->
                label.styleClass.remove("text-dark")
            }

            allIcons.forEach { icon ->
                icon.styleClass.remove("icon-dark")
            }

            // Remove dark mode from common container types
            applyDarkModeToContainers(parent, false)
        }
    }

    private fun applyDarkModeToContainers(parent: Parent, darkMode: Boolean) {
        findAllContainers(parent).forEach { container ->
            when {
                container.styleClass.contains("upperleft-pane") || container.styleClass.contains("upperleft-pane-dark") -> {
                    if (darkMode) {
                        container.styleClass.add("upperleft-pane-dark")
                    } else {
                        container.styleClass.remove("upperleft-pane-dark")
                    }
                }

                container.styleClass.contains("sidebar") || container.styleClass.contains("sidebar-dark") -> {
                    if (darkMode) {
                        container.styleClass.remove("sidebar")
                        container.styleClass.add("sidebar-dark")
                    } else {
                        container.styleClass.remove("sidebar-dark")
                        container.styleClass.add("sidebar")
                    }
                }
                container.styleClass.contains("navbar") || container.styleClass.contains("navbar-dark") -> {
                    if (darkMode) {
                        container.styleClass.remove("navbar")
                        container.styleClass.add("navbar-dark")
                    } else {
                        container.styleClass.remove("navbar-dark")
                        container.styleClass.add("navbar")
                    }
                }
                container.styleClass.contains("main-pane") || container.styleClass.contains("main-pane-dark") -> {
                    if (darkMode) {
//                        container.styleClass.remove("main-pane")
                        if (!container.styleClass.contains("main-pane-dark")) {
                            container.styleClass.add("main-pane-dark")
                        }
                    } else {
                        container.styleClass.remove("main-pane-dark")
                    }
                }
//                container.styleClass.contains("right-pane") || container.styleClass.contains("right-pane-dark") -> {
//                    if (darkMode) {
//                        container.styleClass.add("right-pane-dark")
//                    } else {
//                        container.styleClass.remove("right-pane-dark")
//                    }
//                }
                container.styleClass.contains("calendar") || container.styleClass.contains("calendar-dark") -> {
                    if (darkMode) {
                        container.styleClass.add("calendar-dark")
                    } else {
                        container.styleClass.remove("calendar-dark")
                    }
                }
                // TODO: Verify if might not need or no
                container.styleClass.contains("inner-pane") || container.styleClass.contains("inner-pane-dark") -> {
                    if (darkMode) {
                        container.styleClass.add("inner-pane-dark")
                    } else {
                        container.styleClass.remove("inner-pane-dark")
                    }
                }
                container.styleClass.contains("grid-supply") || container.styleClass.contains("grid-supply-dark") -> {
                    if (darkMode) {
                        container.styleClass.remove("grid-supply")
                        if (!container.styleClass.contains("grid-supply-dark")) {
                            container.styleClass.add("grid-supply-dark")
                        }
                    } else {
                        container.styleClass.remove("grid-supply-dark")
                        if (!container.styleClass.contains("grid-supply")) {
                            container.styleClass.add("grid-supply")
                        }
                    }
                }

                container.styleClass.contains("supply-box") || container.styleClass.contains("supply-box-dark") -> {
                    if (darkMode) {
                        container.styleClass.remove("supply-box")
                        if (!container.styleClass.contains("supply-box-dark")) {
                            container.styleClass.add("supply-box-dark")
                        }
                    } else {
                        container.styleClass.remove("supply-box-dark")
                        if (!container.styleClass.contains("supply-box")) {
                            container.styleClass.add("supply-box")
                        }
                    }
                }
                // TableView dark mode
                container.styleClass.contains("table-view") || container.styleClass.contains("table-view-dark") -> {
                    if (darkMode) {
                        if (!container.styleClass.contains("table-view-dark")) {
                            container.styleClass.add("table-view-dark")
                        }
                    } else {
                        container.styleClass.remove("table-view-dark")
                    }
                }
            }

            // Handle navigation panes
            if (container is FlowPane) {
                when {
                    container.styleClass.any { it.contains("navbar-pane") } -> {
                        if (darkMode) {
                            container.styleClass.add("navbar-pane-dark")
                        } else {
                            container.styleClass.remove("navbar-pane-dark")
                        }
                    }
                    container.styleClass.any { it.contains("sidebar-pane") } -> {
                        if (darkMode) {
                            container.styleClass.add("sidebar-pane-dark")
                        } else {
                            container.styleClass.remove("sidebar-pane-dark")
                        }
                    }
                }

                // Handle active states
                if (darkMode && container.styleClass.contains("sidebar-pane-active")) {
                    container.styleClass.remove("sidebar-pane-active")
                    container.styleClass.add("sidebar-pane-active-dark")
                } else if (!darkMode && container.styleClass.contains("sidebar-pane-active-dark")) {
                    container.styleClass.remove("sidebar-pane-active-dark")
                    container.styleClass.add("sidebar-pane-active")
                }
            }
        }

        // Handle spinners
        findAllSpinners(parent).forEach { spinner ->
            if (darkMode) {
                spinner.styleClass.add("spinner-dark")
            } else {
                spinner.styleClass.remove("spinner-dark")
            }
        }

        // Handle buttons with dark mode variants
        findAllButtons(parent).forEach { button ->
            when {
                button.styleClass.contains("login-button") || button.styleClass.contains("login-button-dark") -> {
                    if (darkMode) {
                        button.styleClass.remove("login-button")
                        button.styleClass.add("login-button-dark")
                    } else {
                        button.styleClass.remove("login-button-dark")
                        button.styleClass.add("login-button")
                    }
                }
                button.styleClass.contains("main-button-reversed") || button.styleClass.contains("main-button-reversed-dark") -> {
                    if (darkMode) {
                        button.styleClass.remove("main-button-reversed")
                        button.styleClass.add("main-button-reversed-dark")
                    } else {
                        button.styleClass.remove("main-button-reversed-dark")
                        button.styleClass.add("main-button-reversed")
                    }
                }
                button.styleClass.contains("sub-button") || button.styleClass.contains("sub-button-dark") -> {
                    if (darkMode) {
                        button.styleClass.remove("sub-button")
                        button.styleClass.add("sub-button-dark")
                    } else {
                        button.styleClass.remove("sub-button-dark")
                        button.styleClass.add("sub-button")
                    }
                }
                button.styleClass.contains("confirm-button") || button.styleClass.contains("confirm-button-dark") -> {
                    if (darkMode) {
                        button.styleClass.remove("confirm-button")
                        button.styleClass.add("confirm-button-dark")
                    } else {
                        button.styleClass.remove("confirm-button-dark")
                        button.styleClass.add("confirm-button")
                    }
                }
            }
        }

        // Handle other elements with dark mode variants
        findAllNodes(parent).forEach { node ->
            when {
                node.styleClass.contains("file-img") || node.styleClass.contains("file-img-dark") -> {
                    if (darkMode) {
                        node.styleClass.remove("file-img")
                        node.styleClass.add("file-img-dark")
                    } else {
                        node.styleClass.remove("file-img-dark")
                        node.styleClass.add("file-img")
                    }
                }
            }
        }

        // Handle labels with special styling
        findAllLabels(parent).forEach { label ->
            if (darkMode && label.styleClass.contains("login-subtitle")) {
                label.styleClass.remove("login-subtitle")
                label.styleClass.add("login-subtitle-dark")
            } else if (!darkMode && label.styleClass.contains("login-subtitle-dark")) {
                label.styleClass.remove("login-subtitle-dark")
                label.styleClass.add("login-subtitle")
            }

            if (darkMode && label.styleClass.contains("underline-label")) {
                label.styleClass.remove("underline-label")
                label.styleClass.add("underline-label-dark")
            } else if (!darkMode && label.styleClass.contains("underline-label-dark")) {
                label.styleClass.remove("underline-label-dark")
                label.styleClass.add("underline-label")
            }

            // Handle highlight labels
            if (darkMode && label.styleClass.contains("highlight")) {
                label.styleClass.remove("highlight")
                label.styleClass.add("highlight-dark")
            } else if (!darkMode && label.styleClass.contains("highlight-dark")) {
                label.styleClass.remove("highlight-dark")
                label.styleClass.add("highlight")
            }

            // Handle title labels
            if (darkMode && label.styleClass.contains("title")) {
                label.styleClass.add("title-dark")
            } else if (!darkMode && label.styleClass.contains("title-dark")) {
                label.styleClass.remove("title-dark")
            }

            // Handle supply labels
            if (darkMode && label.styleClass.contains("supply-label")) {
                label.styleClass.remove("supply-label")
                label.styleClass.add("supply-label-dark")
            } else if (!darkMode && label.styleClass.contains("supply-label-dark")) {
                label.styleClass.remove("supply-label-dark")
                label.styleClass.add("supply-label")
            }
        }

        // Handle FontIcons with special styling
        findAllFontIcons(parent).forEach { icon ->
            when {
                icon.styleClass.contains("back-icon") || icon.styleClass.contains("back-icon-dark") -> {
                    if (darkMode) {
                        icon.styleClass.remove("back-icon")
                        icon.styleClass.add("back-icon-dark")
                    } else {
                        icon.styleClass.remove("back-icon-dark")
                        icon.styleClass.add("back-icon")
                    }
                }
            }
        }
    }

    private fun findAllContainers(parent: Parent): List<Parent> {
        val result = mutableListOf<Parent>()

        // Include the parent element itself first
        result.add(parent)

        // Then recursively find all child containers
        for (node in parent.childrenUnmodifiable) {
            if (node is Parent) {
                result.addAll(findAllContainers(node))
            }
        }
        return result
    }
    fun setupPassword(passTextField: TextField, passPasswordField: PasswordField,
                      showPassButton: Button
    ): () -> Unit {
        var isPasswordShown = false
        passTextField.isVisible = false

        val toggleAction = {
            if (!isPasswordShown) {
                passTextField.text = passPasswordField.text
                passPasswordField.isVisible = false
                passTextField.isVisible = true
            } else {
                passPasswordField.text = passTextField.text
                passTextField.isVisible = false
                passPasswordField.isVisible = true
            }
            isPasswordShown = !isPasswordShown
        }

        showPassButton.setOnAction { toggleAction() }
        return toggleAction
    }

    fun findAllFontIcons(parent: Parent): List<FontIcon> {
        val result = mutableListOf<FontIcon>()
        for (node in parent.childrenUnmodifiable) {
            if (node is FontIcon) {
                result.add(node)
            }
            if (node is Parent) {
                result.addAll(findAllFontIcons(node))
            }
        }
        return result
    }

    fun findAllLabels(parent: Parent): List<Label> {
        val result = mutableListOf<Label>()
        for (node in parent.childrenUnmodifiable) {
            if (node is Label) {
                result.add(node)
            }
            if (node is Parent) {
                result.addAll(findAllLabels(node))
            }
        }
        return result
    }

    private fun findAllSpinners(parent: Parent): List<javafx.scene.control.Spinner<*>> {
        val result = mutableListOf<javafx.scene.control.Spinner<*>>()
        for (node in parent.childrenUnmodifiable) {
            if (node is javafx.scene.control.Spinner<*>) {
                result.add(node)
            }
            if (node is Parent) {
                result.addAll(findAllSpinners(node))
            }
        }
        return result
    }

    private fun findAllButtons(parent: Parent): List<Button> {
        val result = mutableListOf<Button>()
        for (node in parent.childrenUnmodifiable) {
            if (node is Button) {
                result.add(node)
            }
            if (node is Parent) {
                result.addAll(findAllButtons(node))
            }
        }
        return result
    }

    private fun findAllNodes(parent: Parent): List<javafx.scene.Node> {
        val result = mutableListOf<javafx.scene.Node>()
        for (node in parent.childrenUnmodifiable) {
            result.add(node)
            if (node is Parent) {
                result.addAll(findAllNodes(node))
            }
        }
        return result
    }

}