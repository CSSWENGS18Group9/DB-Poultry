package org.db_poultry.controller

import org.db_poultry.util.GeneralUtil
import org.db_poultry.util.flockDateSingleton

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ChoiceBox
import javafx.scene.layout.AnchorPane
import org.db_poultry.db.DBConnect
import org.db_poultry.db.flockDAO.ReadFlock
import java.net.URL
import java.sql.Date
import java.util.ResourceBundle
import kotlin.collections.isNotEmpty

// TODO: Remove dropdown, replace with gridlike structure @Dattebayo2505
class ViewController: Initializable {

    @FXML
    lateinit var selectFlockAnchorPane: AnchorPane

    @FXML
    private lateinit var viewSelectFlockChoiceBox: ChoiceBox<Any>

    val data = flockDateSingleton.instance

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

    @FXML
    fun switchToViewFlockDetails() {
        data.setDate(Date.valueOf(viewSelectFlockChoiceBox.value as String))
        GeneralUtil.navigateToMainContent(selectFlockAnchorPane, "/fxml/content_view_flock_details.fxml")
    }
}
