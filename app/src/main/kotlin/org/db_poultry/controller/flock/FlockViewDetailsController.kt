package org.db_poultry.controller.flock

import org.db_poultry.util.GeneralUtil
import org.db_poultry.util.FlockSingleton
import org.db_poultry.db.flockDetailsDAO.ReadFlockDetails
import org.db_poultry.pojo.FlockPOJO.FlockDetails
import org.db_poultry.db.reportDAO.ReadMortalityRate
import org.db_poultry.db.DBConnect.getConnection

import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.AnchorPane
import java.net.URL
import java.sql.Date
import java.util.*

class FlockViewDetailsController : Initializable {

    @FXML
    lateinit var mainAnchorPane: AnchorPane

    @FXML
    lateinit var flockNameLabel: Label

    @FXML
    lateinit var dateStartedLabel: Label

    @FXML
    lateinit var quantityStartedLabel: Label

    @FXML
    lateinit var mortalityRateLabel: Label

    @FXML
    lateinit var currentCountLabel: Label

    @FXML
    lateinit var flockRecordsTableView: TableView<FlockDetails>

    @FXML
    lateinit var colDate: TableColumn<FlockDetails, Date>

    @FXML
    lateinit var colDepletion: TableColumn<FlockDetails, Int>

    @FXML
    lateinit var colChickenCount: TableColumn<FlockDetails, Int>

    private val currentFlockComplete = FlockSingleton.getCurrentFlockComplete()
    private var cumulativeDepletion: List<Int> = emptyList()
    private val startingDate = currentFlockComplete?.flock?.startingDate
    private val startingCount = currentFlockComplete?.flock?.startingCount

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        setupLabels()
        setupTableColumns()
        loadFlockDetails()
        clearTableSelection()
    }

    private fun setupLabels() {

        val mortalityRateData = ReadMortalityRate.calculateMortalityRateForFlock(
            getConnection(),
            currentFlockComplete!!.flock.startingDate
        )

        flockNameLabel.text = GeneralUtil.formatDatePretty(startingDate?.toLocalDate())
        dateStartedLabel.text = startingDate.toString()
        quantityStartedLabel.text = startingCount.toString()
        mortalityRateLabel.text = "${"%.2f".format(mortalityRateData.mortalityRate)}%"
        currentCountLabel.text = "${mortalityRateData.curCount}"
    }

    private fun setupTableColumns() {
        colDate.cellValueFactory = PropertyValueFactory("fdDate")
        colDepletion.cellValueFactory = PropertyValueFactory("depletedCount")
    }

    private fun loadFlockDetails() {
        val latestDetail = ReadFlockDetails.getMostRecent(getConnection(), startingDate)

        if (latestDetail != null) {
            val flockDetailsList = currentFlockComplete!!.flockDetails.sortedBy { it.fdDate }
            computeCumulativeDepletion(flockDetailsList)
            populateTable(flockDetailsList)
            setupChickenCountColumn()
        }
    }

    private fun computeCumulativeDepletion(flockDetailsList: List<FlockDetails>) {
        cumulativeDepletion = flockDetailsList.runningFold(0) { acc, detail ->
            acc + detail.depletedCount
        }.drop(1)
    }

    private fun setupChickenCountColumn() {
        colChickenCount.setCellValueFactory { cellData ->
            val rowIndex = flockRecordsTableView.items.indexOf(cellData.value)

            val remainingChickens: Int = if (rowIndex >= 0 && rowIndex < cumulativeDepletion.size) {
                (startingCount ?: 0) - cumulativeDepletion[rowIndex]
            } else {
                startingCount ?: 0
            }

            SimpleIntegerProperty(remainingChickens).asObject()
        }
    }

    private fun populateTable(flockDetailsList: List<FlockDetails>) {
        val observableList = FXCollections.observableArrayList(flockDetailsList)
        flockRecordsTableView.items = observableList
    }

    private fun clearTableSelection() {
        flockRecordsTableView.selectionModel.clearSelection()
        flockRecordsTableView.focusModel.focus(-1)
    }

    @FXML
    fun backToViewFlocks() {
        GeneralUtil.navigateToMainContent(mainAnchorPane, "/fxml/content_view_flock.fxml")
    }
}