package org.db_poultry.controller.supply

import org.db_poultry.db.DBConnect.getConnection
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
import java.util.*

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.db_poultry.util.SupplySingleton
import java.io.File
import kotlin.toString

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
        suppliesLabel.text = SupplySingleton.getCurrentSupplyName()

        val isDefaultSupplyType = SupplySingleton.isDefaultSupplyType(SupplySingleton.getCurrentSupplyName())

        val imageUrl = if (isDefaultSupplyType) {
            javaClass.getResource(SupplySingleton.getCurrentSupplyImageDir())
        } else {
            File(SupplySingleton.getCurrentSupplyImageDir()).toURI().toURL()
        }

        val image = if (imageUrl != null) {
            Image(imageUrl.toString(), true)
        } else {
            Image(File(SupplySingleton.getUIDefaultImagePath()).toURI().toURL().toString(), true)
        }

        supplyImageView.image = image
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
        val currentSupplyName = SupplySingleton.getCurrentSupplyName()
        val supplyList = ReadSupplyRecord.getFromName(getConnection(), currentSupplyName)
        if (supplyList != null) {
            val observableList = FXCollections.observableArrayList(supplyList)
            supplyViewTable.items = observableList
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