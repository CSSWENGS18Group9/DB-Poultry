package org.db_poultry.controller.supply

import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyTypeDAO.ReadSupplyType
import org.db_poultry.db.supplyRecordDAO.ReadSupplyRecord
import org.db_poultry.pojo.SupplyPOJO.SupplyComplete

import org.db_poultry.util.GeneralUtil

import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.AnchorPane
import java.math.BigDecimal
import java.net.URL
import java.time.LocalDate
import java.util.*
import javafx.event.ActionEvent

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty

class SuppliesViewController: Initializable {

    @FXML
    private lateinit var currDateLbl: Label

    @FXML
    private lateinit var viewSupplyAnchorPane: AnchorPane

    @FXML
    private lateinit var viewSuppliesTable: TableView<SupplyWithCurrentAmount>

    @FXML
    private lateinit var supplyTypeCol: TableColumn<SupplyWithCurrentAmount, String>

    @FXML
    private lateinit var qtyAddedCol: TableColumn<SupplyWithCurrentAmount, BigDecimal>

    @FXML
    private lateinit var qtyDepletedCol: TableColumn<SupplyWithCurrentAmount, BigDecimal>

    @FXML
    private lateinit var currAmountCol: TableColumn<SupplyWithCurrentAmount, BigDecimal>

    data class SupplyWithCurrentAmount(
        val supply: SupplyComplete,
        val currentAmount: BigDecimal
    )

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // Set up table columns
        supplyTypeCol.setCellValueFactory { cellData ->
            SimpleStringProperty(cellData.value.supply.supply_name)
        }
        qtyAddedCol.setCellValueFactory { cellData ->
            SimpleObjectProperty(cellData.value.supply.added)
        }
        qtyDepletedCol.setCellValueFactory { cellData ->
            SimpleObjectProperty(cellData.value.supply.consumed)
        }
        currAmountCol.setCellValueFactory { cellData ->
            SimpleObjectProperty(cellData.value.currentAmount)
        }

        // Set current date in label
        val today = LocalDate.now()
        currDateLbl.text = "Current Date: $today"
        
        // Fetch and display supply data
        loadSupplyData()
    }

    private fun loadSupplyData() {
        val supplyTypes = ReadSupplyType.getAllSupplyTypes(getConnection()) ?: return
        val tableData = FXCollections.observableArrayList<SupplyWithCurrentAmount>()

        for (supplyType in supplyTypes) {
            val records = ReadSupplyRecord.getFromName(getConnection(), supplyType.name)
            if (records != null) {
                // Sort records by date
                val sortedRecords = records.sortedBy { it.date }
                var runningTotal = BigDecimal.ZERO

                for (record in sortedRecords) {
                    val added = record.added ?: BigDecimal.ZERO
                    val consumed = record.consumed ?: BigDecimal.ZERO

                    runningTotal = if (record.isRetrieved) {
                        added - consumed
                    } else {
                        runningTotal + added - consumed
                    }

                    tableData.add(SupplyWithCurrentAmount(record, runningTotal.setScale(4)))
                }
            }
        }
        viewSuppliesTable.items = tableData
    }

    @FXML
    fun navigateToViewSupplies(event: ActionEvent) {
        GeneralUtil.navigateToMainContent(viewSupplyAnchorPane, "/fxml/content_view_supplies.fxml")
    }
        
    @FXML
    fun navigateToViewSupplyHistory(event: ActionEvent) {
        GeneralUtil.navigateToMainContent(viewSupplyAnchorPane, "/fxml/content_view_supply_history.fxml")
    }

    @FXML
    fun navigateToViewDateHistory(event: ActionEvent) {
        GeneralUtil.navigateToMainContent(viewSupplyAnchorPane, "/fxml/content_view_date_history.fxml")
    }
}