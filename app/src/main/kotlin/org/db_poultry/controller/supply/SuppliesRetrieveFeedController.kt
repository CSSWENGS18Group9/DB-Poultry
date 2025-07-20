package org.db_poultry.controller.supply

import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyTypeDAO.ReadSupplyType

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.shape.Rectangle
import javafx.scene.control.DatePicker

import javafx.fxml.Initializable
import org.db_poultry.db.supplyRecordDAO.ReadSupplyRecord
import java.net.URL
import java.util.ResourceBundle 

class SuppliesRetrieveFeedController: Initializable {

    @FXML
    private lateinit var btnConfirm: Button

    @FXML
    private lateinit var supplyTypeComboBox: ComboBox<String>

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
        // TODO: @Dattebayo2505
        // Fix this, use CreateSupplyRecord with any value for added or depleted but SET Retrieved to TRUE.
        // This handles all the columns for you already!
        
        val supplyTypeName = supplyTypeComboBox.value
        val date = ReadSupplyRecord.getMostRecentFromName(getConnection(), supplyTypeName).date

        if (supplyTypeName == null || supplyTypeName == "Select a Supply Type" || date == null) {
            println("Please select a supply type and date.")
            return
        }

//        CreateSupplyRecord.createSupplyRecord(getConnection(), , , , , )

        println("Supply retrieved successfully: $supplyTypeName on $date") // DEBUGGING
    }
}
