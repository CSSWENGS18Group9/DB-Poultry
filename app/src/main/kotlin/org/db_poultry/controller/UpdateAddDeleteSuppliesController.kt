package org.db_poultry.controller

import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyTypeDAO.ReadSupplyType
import org.db_poultry.db.supplyRecordDAO.CreateSupplyRecord.createSupplyRecord

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.AnchorPane
import javafx.scene.shape.Rectangle

import java.math.BigDecimal
import java.sql.Date

import javafx.fxml.Initializable
import java.net.URL
import java.util.ResourceBundle

class UpdateAddDeleteSuppliesController: Initializable {

    @FXML
    private lateinit var addRadioBtn: RadioButton

    @FXML
    private lateinit var amountTxtField: TextField

    @FXML
    private lateinit var btnConfirm: Button

    @FXML
    private lateinit var datepickerDate: DatePicker

    @FXML
    private lateinit var deletedRadioBtn: RadioButton

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

    @FXML
    private lateinit var supplyTypeComboBox: ComboBox<String>

    @FXML
    private lateinit var toggle: ToggleGroup

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        val supplyTypesArrayList = ReadSupplyType.getAllSupplyTypes(getConnection())

        val supplyTypeNames: List<String> = supplyTypesArrayList?.map { it.name } ?: emptyList()

        supplyTypeComboBox.items.clear()
        if (supplyTypeNames.isNotEmpty()) {
            supplyTypeComboBox.items.addAll(supplyTypeNames)
        } else {
            println("No supply types available.")
            supplyTypeComboBox.items.add("No Supply Types Available")
        }

        supplyTypeComboBox.items.clear()
        supplyTypeComboBox.items.addAll(supplyTypeNames)
        supplyTypeComboBox.value = "Select a Supply Type"

    }

    @FXML
    fun confirm() {
        val supplyType = supplyTypeComboBox.value
        var amount_add = amountTxtField.text.toIntOrNull()
        var amount_del = amount_add
        val date = datepickerDate.value

        if (supplyType == null || amount_add == null || date == null) {
            println("Please fill in all fields correctly.")
            return
        }

        if (addRadioBtn.isSelected) {
            amount_del = 0
            println("Adding $amount_add of $supplyType on $date")
        } else if (deletedRadioBtn.isSelected) {
            amount_add = 0
            println("Deleting $amount_del of $supplyType on $date")
        } else {
            println("Please select an action (Add/Delete).")
        }

        val supplyID = ReadSupplyType.getSupplyTypeByName(getConnection(), supplyType.toString())?.getSupplyTypeId() ?: return

        val sqlDate = Date.valueOf(date)
        val added = BigDecimal(amount_add)
        val consumed = BigDecimal(amount_del)

        createSupplyRecord(getConnection(), supplyID, sqlDate, added, consumed, false)


    }

}
