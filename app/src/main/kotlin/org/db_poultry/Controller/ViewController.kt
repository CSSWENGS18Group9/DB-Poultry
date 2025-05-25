package org.db_poultry.Controller

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.text.Text

import org.db_poultry.Util.SceneSwitcher

class ViewController {

    @FXML
    private lateinit var confirmFlockBtn: Button

    @FXML
    fun switchToViewFlockDetails() {
        println("hello")
        SceneSwitcher.switchTo(confirmFlockBtn, "/content_view_flock_details.fxml")
    }

    @FXML
    private lateinit var selectFlockChoiceBox: ChoiceBox<Any>

    @FXML
    private lateinit var selectFlockLbl: Text

}
