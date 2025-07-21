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
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.ImageView
import org.db_poultry.util.SupplyTypeSingleton
import java.io.File

class SuppliesViewController: Initializable {

    @FXML
    private lateinit var viewSupplyAnchorPane: AnchorPane

    @FXML
    private lateinit var supplyImageView: ImageView

    @FXML
    private lateinit var suppliesLabel: Label

    @FXML
    private lateinit var supplyViewTable: TableView<SupplyComplete>

    @FXML
    private lateinit var dateCol: TableColumn<SupplyComplete, Date>

    @FXML
    private lateinit var currQtyCol: TableColumn<SupplyComplete, BigDecimal>

    @FXML
    private lateinit var qtyAddedCol: TableColumn<SupplyComplete, BigDecimal>

    @FXML
    private lateinit var qtyConsumedCol: TableColumn<SupplyComplete, BigDecimal>

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        setSupply()
        setupTableColumns()
        loadSupplyData()

    }

    private fun setSupply() {
        suppliesLabel.text = SupplyTypeSingleton.getCurrentSupply()
        val supplyImage = SupplyTypeSingleton.getCurrentSupplyImageDir() ?: SupplyTypeSingleton.getUIDefaultImagePath()
        supplyImageView = ImageView(File(supplyImage).toURI().toString())
    }

    private fun setupTableColumns() {
        dateCol.cellValueFactory = PropertyValueFactory("date")
        qtyAddedCol.cellValueFactory = PropertyValueFactory("added")
        qtyConsumedCol.cellValueFactory = PropertyValueFactory("consumed")
        currQtyCol.setCellValueFactory { cellData: TableColumn.CellDataFeatures<SupplyComplete, BigDecimal> ->
            val currentQty = ReadSupplyRecord.getCurrentCountForDate(
                getConnection(),
                cellData.value.supply_type_id,
                cellData.value.date
            )
            SimpleObjectProperty(currentQty)
        }
    }

    private fun loadSupplyData() {
        val currentSupplyName = SupplyTypeSingleton.getCurrentSupply()
        if (currentSupplyName != null) {
            val supplyList = ReadSupplyRecord.getFromName(getConnection(), currentSupplyName)
            if (supplyList != null) {
                val observableList = FXCollections.observableArrayList(supplyList)
                supplyViewTable.items = observableList
            }
        }
    }

    @FXML
    fun navigateToViewSupplies() {
        GeneralUtil.navigateToMainContent(viewSupplyAnchorPane, "/fxml/content_view_supplies.fxml")
    }

    @FXML
    fun backToSuppliesGridHome() {
        GeneralUtil.navigateToMainContent(viewSupplyAnchorPane, "/fxml/content_home_supplies_grid.fxml")
    }

}