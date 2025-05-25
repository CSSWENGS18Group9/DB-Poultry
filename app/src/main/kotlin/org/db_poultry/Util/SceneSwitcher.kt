package org.db_poultry.Util

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.scene.Node

import javafx.scene.layout.BorderPane


/**
    * Switches the current scene to the one defined by the given FXML file.
    *
    * @param node A JavaFX Node from which the current Stage is derived.
    * @param fxml The absolute path to the FXML file (e.g., "/home.fxml").
    *
    * Example usage:
    *     SceneSwitcher.switchTo(button, "/home.fxml")
    */
object SceneSwitcher {
    /**
     * Switches the entire scene (use only for login/logout screens).
     * WARNING: This will remove the sidebar. For navigation within the main app,
     * use switchContent() instead.
     */
    fun switchTo(node: Node, fxml: String) {
        val stage = node.scene.window as Stage
        val root: Parent = FXMLLoader(javaClass.getResource(fxml)).load()
        stage.scene = Scene(root)
    }
    
    /**
     * Switches only the content area while preserving the sidebar.
     * Use this for all navigation within the main application.
     */
    fun switchContent(node: Node, fxml: String) {
        try {
            // Find the BorderPane in the scene graph
            var current: Parent = node.parent
            while (current !is BorderPane && current.parent != null) {
                current = current.parent as Parent
            }
            
            if (current is BorderPane) {
                val borderPane = current as BorderPane
                val view = FXMLLoader(javaClass.getResource(fxml)).load<Parent>()
                borderPane.center = view
            } else {
                // Fallback to switching entire scene if BorderPane not found
                println("Warning: BorderPane not found, falling back to full scene switch")
                switchTo(node, fxml)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}