package org.db_poultry.controller

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyTypeDAO.CreateSupplyType.createSupplyType

import javafx.fxml.Initializable
import java.net.URL
import java.util.ResourceBundle

class CreateSuppliesController: Initializable {

    @FXML
    private lateinit var anchorPane: AnchorPane

    @FXML
    private lateinit var btnConfirm: Button

    @FXML
    private lateinit var createSuppliesTextField: TextField

    @FXML
    private lateinit var createSuppliesTextFieldUnit: TextField

    @FXML
    private lateinit var shapeBg: Rectangle

    @FXML
    private lateinit var textBody1: Text

    @FXML
    private lateinit var textBody2: Text

    @FXML
    private lateinit var textHeader: Text

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // Initialization logic if needed
    }

    @FXML
    fun confirm() {

        val supplyName = createSuppliesTextField.text
        val supplyUnit = createSuppliesTextFieldUnit.text

        println("\nSupply Name: $supplyName")
        println("Supply Unit: $supplyUnit")

        if (createSupplyType(getConnection(), supplyName, supplyUnit) != null) {
            println("Successfully created Supply type.")
        }
        else {
            println("Failed to create Supply type.")
        }

    }

}