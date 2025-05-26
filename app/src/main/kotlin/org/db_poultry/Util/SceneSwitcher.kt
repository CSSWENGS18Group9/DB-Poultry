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
        try {
            val stage = node.scene.window as Stage
            val loader = FXMLLoader(javaClass.getResource(fxml))
            loader.controllerFactory = ControllerManager.controllerFactory
            val root: Parent = loader.load()
            stage.scene = Scene(root)
        } catch (e: Exception) {
            println("Error switching scene: ${e.message}")
            e.printStackTrace()
        }
    }
}