package org.db_poultry.Controller

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.event.ActionEvent

import javafx.scene.control.Button
import javafx.scene.text.Text
import javafx.scene.control.TextField

class LoginController {

    @FXML
    private lateinit var btnSample: Button

    @FXML
    private lateinit var textSample: Text

    @FXML
    private lateinit var tfPassword: TextField

    @FXML
    private lateinit var tfUN: TextField
    
    fun switchToHome() {
        println("Switching to Home")
        try {
            val loader = FXMLLoader(javaClass.getResource("/home.fxml"))
            val homeRoot: Parent = loader.load()

            val stage = btnSample.scene.window as Stage

            stage.scene = Scene(homeRoot)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
