package org.db_poultry.controller.supply

import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyTypeDAO.ReadSupplyType

import javafx.fxml.FXML
import javafx.scene.control.Label

import javafx.fxml.Initializable
import javafx.scene.control.Button
import org.db_poultry.db.supplyRecordDAO.CreateSupplyRecord
import org.db_poultry.db.supplyRecordDAO.ReadSupplyRecord
import java.math.BigDecimal
import java.net.URL
import java.time.LocalDate
import java.sql.Date
import java.util.ResourceBundle
import kotlin.text.compareTo

class SuppliesRetrieveFeedController: Initializable {

    @FXML
    private lateinit var currCountBoosterLabel: Label

    @FXML
    private lateinit var currCountStarterLabel: Label

    @FXML
    private lateinit var currCountGrowerLabel: Label

    @FXML
    private lateinit var currCountFinisherLabel: Label

    @FXML
    private lateinit var confirmButton: Button

    private val sqlDate: Date = Date.valueOf(LocalDate.now())
    private val feedArrayList: List<String> = listOf("Booster Feed", "Starter Feed", "Grower Feed", "Finisher Feed")
    private lateinit var feedSupplyTypeIDList: MutableList<Int>

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        feedSupplyTypeIDList = mutableListOf()
        setFeedCounts()


    }

    private fun setFeedCounts() {
        val feedCountMap = HashMap<String, BigDecimal>()

        for (feed in feedArrayList) {
            val supplyTypeID = ReadSupplyType.getSupplyTypeByName(getConnection(), feed).supplyTypeId
            feedSupplyTypeIDList.add(supplyTypeID)
            val count = ReadSupplyRecord.getCurrentCountForDate(getConnection(), supplyTypeID, sqlDate)
            feedCountMap[feed] = count
        }

        currCountBoosterLabel.text = "Current Count: ${feedCountMap["Booster Feed"] ?: "ERROR"}"
        currCountStarterLabel.text = "Current Count: ${feedCountMap["Starter Feed"] ?: "ERROR"}"
        currCountGrowerLabel.text = "Current Count: ${feedCountMap["Grower Feed"] ?: "ERROR"}"
        currCountFinisherLabel.text = "Current Count: ${feedCountMap["Finisher Feed"] ?: "ERROR"}"

        // Disalble confirm button if all counts are zero
        val allCountsZero = feedCountMap.values.all { it.compareTo(BigDecimal.ZERO) == 0 }
        confirmButton.isDisable = allCountsZero

    }

    @FXML
    fun confirm() {
        for (supplyTypeID in feedSupplyTypeIDList) {
            CreateSupplyRecord.createSupplyRecord(getConnection(), supplyTypeID,
                sqlDate, BigDecimal.ZERO, BigDecimal.ZERO, true)
        }

        setFeedCounts()
    }
}
