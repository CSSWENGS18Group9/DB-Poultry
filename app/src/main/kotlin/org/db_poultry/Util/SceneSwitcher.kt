package org.db_poultry.Util

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.scene.Node

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
    fun switchTo(node: Node, fxml: String) {
        val stage = node.scene.window as Stage
        val root: Parent = FXMLLoader(javaClass.getResource(fxml)).load()
        stage.scene = Scene(root)
    }
}