package org.db_poultry.Util

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.scene.Node

object SceneSwitcher {
    fun switchTo(node: Node, fxml: String) {
        val stage = node.scene.window as Stage
        val root: Parent = FXMLLoader(javaClass.getResource(fxml)).load()
        stage.scene = Scene(root)
    }
}