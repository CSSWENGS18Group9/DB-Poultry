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

class HomeController {

    @FXML
    private lateinit var btnBackToLogin: Button

    @FXML
    private lateinit var tfHomeMenu: TextField

    @FXML
    private lateinit var txtHomeMenu: Text

    @FXML
    fun switchToHome(event: ActionEvent) {
        println("Switching to Login")
        try {
            val loader = FXMLLoader(javaClass.getResource("/login.fxml"))
            val homeRoot: Parent = loader.load()

            val stage = btnBackToLogin.scene.window as Stage

            stage.scene = Scene(homeRoot)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
