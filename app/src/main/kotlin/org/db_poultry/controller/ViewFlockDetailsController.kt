package org.db_poultry.controller

import org.db_poultry.util.GeneralUtil
import org.db_poultry.controller.backend.CurrentFlockInUse
import org.db_poultry.util.flockDateSingleton
import org.db_poultry.db.DBConnect
import org.db_poultry.db.flockDAO.ReadFlock
import org.db_poultry.db.flockDetailsDAO.ReadFlockDetails
import org.db_poultry.pojo.FlockPOJO.Flock
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

class ViewFlockDetailsController : Initializable {

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

//    val data = flockDateSingleton.instance

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        val currentFlock = CurrentFlockInUse.getCurrentFlockComplete()?.flock?.flockId
        val currentFlockDate = CurrentFlockInUse.getCurrentFlockComplete()?.flock?.startingDate
        val currentFlockQuantity = CurrentFlockInUse.getCurrentFlockComplete()?.flock?.startingCount

        flockNameLabel.text = "Flock $currentFlock - ${GeneralUtil.formatDatePretty(currentFlockDate?.toLocalDate())}"

        val latestDetail = ReadFlockDetails.getMostRecent(DBConnect.getConnection(), currentFlockDate)

        dateStartedLabel.text = currentFlockDate.toString()
        quantityStartedLabel.text = currentFlockQuantity.toString()

        // Get data from POJO
        colDate.cellValueFactory = PropertyValueFactory("fdDate")
        colDepletions.cellValueFactory = PropertyValueFactory("depletedCount")

        colChickenCount.setCellValueFactory { cellData ->
            val currentFlockQuantity = CurrentFlockInUse.getCurrentFlockComplete()?.flock?.startingCount ?: 0
            val depletedCount = cellData.value.depletedCount
            javafx.beans.property.SimpleIntegerProperty(currentFlockQuantity - depletedCount).asObject()
        }

        if (latestDetail != null) {
            val flockDetailsList: List<FlockDetails> =
                ReadFlockDetails.getFlockDetailsFromDate(DBConnect.getConnection(), currentFlockDate, currentFlockDate, latestDetail.fdDate)
            val observableList = FXCollections.observableArrayList(flockDetailsList)

            flockRecordsTableView.items = observableList
        }

        // Clear blue highlight focus on first row
        flockRecordsTableView.getSelectionModel().clearSelection()
        flockRecordsTableView.getFocusModel().focus(-1)
    }

    @FXML
    fun backToViewFlocks() {
        GeneralUtil.navigateToMainContent(mainAnchorPane, "/fxml/content_view_flock.fxml")
    }
}