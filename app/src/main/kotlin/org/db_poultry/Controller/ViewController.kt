package org.db_poultry.Controller

import org.db_poultry.Util.SceneSwitcher

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.text.Text


class ViewController {

    @FXML
    private lateinit var viewSelectFlockChoiceBox: ChoiceBox<Any>

    @FXML
    private lateinit var selectFlockLbl: Text

    @FXML
    private lateinit var viewConfirmBtn: Button

    @FXML
    fun switchToViewFlockDetails() {
        println("Switching to view flock details")
        SceneSwitcher.switchContent(viewConfirmBtn, "/fxml/content_view_flock_details.fxml")
    }
}
