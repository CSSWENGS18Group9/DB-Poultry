package org.db_poultry.controller

import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import org.db_poultry.controller.util.flockDateSingleton
import org.db_poultry.db.DBConnect
import org.db_poultry.db.flockDAO.ReadFlock
import org.db_poultry.db.flockDetailsDAO.ReadFlockDetails
import org.db_poultry.pojo.FlockPOJO.Flock
import org.db_poultry.pojo.FlockPOJO.FlockDetails
import java.net.URL
import java.sql.Date
import java.util.*

class ViewFlockDetailsController : Initializable {

    @FXML
    lateinit var viewFlockDetailsAnchorPane: Pane

    @FXML
    lateinit var lblQuantityStarted: Text

    @FXML
    lateinit var lblQuantityStartedValue: Text

    @FXML
    lateinit var lblDateStarted: Text

    @FXML
    lateinit var lblDateStartedValue: Text

    @FXML
    lateinit var tableView: TableView<FlockDetails>

    @FXML
    lateinit var colDate: TableColumn<FlockDetails, Date>

    @FXML
    lateinit var colDepletions: TableColumn<FlockDetails, Int>

    @FXML
    lateinit var lblFlockName: Label

    @FXML
    lateinit var rectFlockDetails: Rectangle

    val data = flockDateSingleton.instance

    override fun initialize(location: URL?, resources: ResourceBundle?) {

        val date = data.getDate()
        val latestDetail = ReadFlockDetails.getMostRecent(DBConnect.getConnection(), date)

        lblDateStartedValue.text = date.toString()
        val flockList: Flock = ReadFlock.getFlockFromADate(DBConnect.getConnection(), date)
        lblQuantityStartedValue.text = flockList.startingCount.toString()

        colDate.cellValueFactory = PropertyValueFactory("fdDate")
        colDepletions.cellValueFactory = PropertyValueFactory("depletedCount")

        if (latestDetail != null) {
            val flockDetailsList: List<FlockDetails> =
                ReadFlockDetails.getFlockDetailsFromDate(DBConnect.getConnection(), date, date, latestDetail.fdDate)
            val observableList = FXCollections.observableArrayList(flockDetailsList)

            tableView.items = observableList
        }
    }

}