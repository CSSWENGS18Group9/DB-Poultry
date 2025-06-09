package org.db_poultry.controller

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text

class CreateSuppliesController {

    @FXML
    private lateinit var anchorPane: AnchorPane

    @FXML
    private lateinit var btnConfirm: Button

    @FXML
    private lateinit var createSuppliesTextField: TextField

    @FXML
    private lateinit var shapeBg: Rectangle

    @FXML
    private lateinit var textBody1: Text

    @FXML
    private lateinit var textBody2: Text

    @FXML
    private lateinit var textHeader: Text

    @FXML
    fun confirm(event: ActionEvent) {

    }

}