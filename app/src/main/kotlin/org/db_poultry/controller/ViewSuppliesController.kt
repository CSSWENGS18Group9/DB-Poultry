package org.db_poultry.controller

import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.AnchorPane
import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyTypeDAO.ReadSupplyType
import org.db_poultry.db.supplyRecordDAO.ReadSupplyRecord
import org.db_poultry.pojo.SupplyPOJO.SupplyComplete
import org.db_poultry.util.GeneralUtil
import java.math.BigDecimal
import java.net.URL
import java.sql.Date
import java.time.LocalDate
import java.util.*
import javafx.event.ActionEvent

class ViewSuppliesController: Initializable {

    @FXML
    private lateinit var currDateLbl: Label

    @FXML
    private lateinit var dateHistoryBtn: Button

    @FXML
    private lateinit var supplyHistoryBtn: Button

    @FXML
    private lateinit var viewSuppliesLbl: Label

    @FXML
    private lateinit var viewSupplyAnchorPane: AnchorPane

    @FXML
    private lateinit var viewSuppliesTable: TableView<SupplyComplete>

    @FXML
    private lateinit var supplyTypeCol: TableColumn<SupplyComplete, String>

    @FXML
    private lateinit var qtyAddedCol: TableColumn<SupplyComplete, BigDecimal>

    @FXML
    private lateinit var qtyDepletedCol: TableColumn<SupplyComplete, BigDecimal>

    @FXML
    private lateinit var currAmountCol: TableColumn<SupplyComplete, BigDecimal>

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // Set up table columns
        supplyTypeCol.setCellValueFactory(PropertyValueFactory("supply_name"))
        qtyAddedCol.setCellValueFactory(PropertyValueFactory("added"))
        qtyDepletedCol.setCellValueFactory(PropertyValueFactory("consumed"))
        
        // Set current date in label
        val today = LocalDate.now()
        currDateLbl.text = "Current Date: $today"
        
        // Fetch and display supply data
        loadSupplyData()
    }
    
    private fun loadSupplyData() {
        val supplyTypes = ReadSupplyType.getAllSupplyTypes(getConnection()) ?: return
        
        val tableData = FXCollections.observableArrayList<SupplyComplete>()
        
        for (supplyType in supplyTypes) {
            val records = ReadSupplyRecord.getFromName(getConnection(), supplyType.getName())
            if (records != null) {
                tableData.addAll(records)
            }
        }
        
        // Populate the table
        viewSuppliesTable.items = tableData
    }
        
    @FXML
    fun switchToViewSupplyHistory(event: ActionEvent) {
        GeneralUtil.loadContentView(viewSupplyAnchorPane, "/fxml/content_view_supply_history.fxml")
    }

    @FXML
    fun switchToViewDateHistory(event: ActionEvent) {
        GeneralUtil.loadContentView(viewSupplyAnchorPane, "/fxml/content_view_date_history.fxml")
    }
}