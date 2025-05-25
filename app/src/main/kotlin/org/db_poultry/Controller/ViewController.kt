package org.db_poultry.Controller

import org.db_poultry.Util.SceneSwitcher

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.text.Text


class ViewController {

    @FXML
    private lateinit var confirmFlockBtn: Button

    @FXML
    fun switchToViewFlockDetails() {
        SceneSwitcher.switchContent(confirmFlockBtn, "/content_view_flock_details.fxml")
    }

    @FXML
    private lateinit var selectFlockChoiceBox: ChoiceBox<Any>

    @FXML
    private lateinit var selectFlockLbl: Text

}
