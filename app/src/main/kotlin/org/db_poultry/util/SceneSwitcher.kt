package org.db_poultry.util

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
    private var primaryStage: Stage? = null

    /**
     * Sets the primary stage reference (call this from your main application class)
     */
    fun setPrimaryStage(stage: Stage) {
        primaryStage = stage
    }

    /**
     * Switches the entire scene using the primary stage reference.
     * WARNING: This will remove the sidebar.
     */
    fun switchTo(fxml: String) {
        try {
            val stage = primaryStage ?: throw IllegalStateException("Primary stage not set")
            val loader = FXMLLoader(javaClass.getResource(fxml))
            loader.controllerFactory = ControllerManager.controllerFactory
            val root: Parent = loader.load()

            val controller = loader.getController<Any>()
            println("Controller for $fxml: ${controller.javaClass.name} (${System.identityHashCode(controller)})")

            stage.scene = Scene(root)
        } catch (e: Exception) {
            println("Error switching scene: ${e.message}")
            e.printStackTrace()
        }
    }
}