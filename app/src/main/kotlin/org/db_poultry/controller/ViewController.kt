package org.db_poultry.controller

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text
import org.db_poultry.util.flockDateSingleton
import org.db_poultry.util.GeneralUtil
import org.db_poultry.db.DBConnect
import org.db_poultry.db.flockDAO.ReadFlock
import java.net.URL
import java.sql.Date
import java.util.ResourceBundle
import kotlin.collections.isNotEmpty


class ViewController: Initializable {

    @FXML
    lateinit var selectFlockAnchorPane: AnchorPane

    @FXML
    private lateinit var viewSelectFlockChoiceBox: ChoiceBox<Any>

    @FXML
    private lateinit var selectFlockLbl: Text

    @FXML
    private lateinit var viewConfirmBtn: Button

    val data = flockDateSingleton.instance

    @FXML
    fun switchToViewFlockDetails() {
        data.setDate(Date.valueOf(viewSelectFlockChoiceBox.value as String))
        println("Switching to view flock details")
        GeneralUtil.loadContentView(selectFlockAnchorPane, "/fxml/content_view_flock_details.fxml")
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {

        val flockMap = ReadFlock.allByID(DBConnect.getConnection())

        val dateList = mutableListOf<String>()

        if (flockMap != null && flockMap.isNotEmpty()) {
            val dates = flockMap.values.map { it.flock.startingDate.toString() }
            dateList.addAll(dates)
            println("Flock starting dates: $dates") // DEBUGGING
        } else {
            println("No data found.")
        }

        viewSelectFlockChoiceBox.items.clear()
        viewSelectFlockChoiceBox.items.addAll(dateList)
        viewSelectFlockChoiceBox.value = "Select a flock"
    }
}
