package org.db_poultry.controller

import org.db_poultry.util.SceneSwitcher
import org.db_poultry.util.GeneralUtil

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.shape.Rectangle
import javafx.scene.control.DatePicker

class UpdateRetrieveSuppliesController {

    @FXML
    private lateinit var btnConfirm: Button

    @FXML
    private lateinit var choiceboxSupply: ChoiceBox<Any>

    @FXML
    private lateinit var datepickerDate: DatePicker

    @FXML
    private lateinit var headerText: Label

    @FXML
    private lateinit var homeAnchorPane: AnchorPane

    @FXML
    private lateinit var labelSelectSupply: Label

    @FXML
    private lateinit var shapeBackground: Rectangle

    @FXML
    private lateinit var shapeSelectSupply: Rectangle

}
