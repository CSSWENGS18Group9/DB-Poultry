package org.db_poultry.controller.flock

import javafx.beans.property.SimpleIntegerProperty
import org.db_poultry.util.GeneralUtil
import org.db_poultry.controller.backend.CurrentFlockInUse
import org.db_poultry.db.DBConnect
import org.db_poultry.db.flockDetailsDAO.ReadFlockDetails
import org.db_poultry.pojo.FlockPOJO.FlockDetails

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
    lateinit var flockRecordsTableView: TableView<FlockDetails>

    @FXML
    lateinit var colDate: TableColumn<FlockDetails, Date>

    @FXML
    lateinit var colDepletions: TableColumn<FlockDetails, Int>

    @FXML
    lateinit var colChickenCount: TableColumn<FlockDetails, Int>

    private var currentFlockData: Triple<Int?, Date?, Int?>? = null
    private var cumulativeDepletions: List<Int> = emptyList()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        initializeFlockData()
        setupLabels()
        setupTableColumns()
        loadFlockDetails()
        clearTableSelection()
    }

    private fun initializeFlockData() {
        val currentFlockComplete = CurrentFlockInUse.getCurrentFlockComplete()
        currentFlockData = Triple(
            currentFlockComplete?.flock?.flockId,
            currentFlockComplete?.flock?.startingDate,
            currentFlockComplete?.flock?.startingCount
        )
    }

    private fun setupLabels() {
        val (flockId, startingDate, startingCount) = currentFlockData ?: return

        flockNameLabel.text = "Flock $flockId - ${GeneralUtil.formatDatePretty(startingDate?.toLocalDate())}"
        dateStartedLabel.text = startingDate.toString()
        quantityStartedLabel.text = startingCount.toString()
    }

    private fun setupTableColumns() {
        colDate.cellValueFactory = PropertyValueFactory("fdDate")
        colDepletions.cellValueFactory = PropertyValueFactory("depletedCount")
    }

    private fun loadFlockDetails() {
        val (_, startingDate, _) = currentFlockData ?: return
        val latestDetail = ReadFlockDetails.getMostRecent(DBConnect.getConnection(), startingDate)

        if (latestDetail != null) {
            val flockDetailsList = getFlockDetailsList(startingDate, latestDetail)
            computeCumulativeDepletions(flockDetailsList)
            setupChickenCountColumn()
            populateTable(flockDetailsList)
        }
    }

    private fun getFlockDetailsList(startingDate: Date?, latestDetail: FlockDetails): List<FlockDetails> {
        return ReadFlockDetails.getFlockDetailsFromDate(
            DBConnect.getConnection(),
            startingDate,
            startingDate,
            latestDetail.fdDate
        )
    }

    private fun computeCumulativeDepletions(flockDetailsList: List<FlockDetails>) {
        cumulativeDepletions = flockDetailsList.runningFold(0) { acc, detail ->
            acc + detail.depletedCount
        }.drop(1)
    }

    private fun setupChickenCountColumn() {
        colChickenCount.setCellValueFactory { cellData ->
            val startingCount = currentFlockData?.third ?: 0
            val rowIndex = flockRecordsTableView.items.indexOf(cellData.value)

            val remainingChickens = if (rowIndex >= 0 && rowIndex < cumulativeDepletions.size) {
                startingCount - cumulativeDepletions[rowIndex]
            } else {
                startingCount
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