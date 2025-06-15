package org.db_poultry.controller

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.shape.Rectangle

class UpdateAddDeleteSuppliesController {

    @FXML
    private lateinit var btnConfirm: Button

    @FXML
    private lateinit var choiceboxSupply1: ChoiceBox<Any>

    @FXML
    private lateinit var choiceboxSupply2: ChoiceBox<Any>

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