package org.db_poultry.controller

import org.db_poultry.controller.flock.BaseFlockViewController
import org.db_poultry.db.DBConnect
import org.db_poultry.db.flockDAO.ReadFlock
import org.db_poultry.pojo.FlockPOJO.FlockComplete
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.layout.AnchorPane
import java.net.URL
import java.util.ResourceBundle

class HomeController : BaseFlockViewController(), Initializable {

    @FXML
    private lateinit var homeAnchorPane: AnchorPane

    override fun initialize(url: URL?, rb: ResourceBundle?) {
        initializeFlockData()
    }

    /**
     * Returns the latest flock (most recent starting date) to display on the home page.
     */
    override fun getFlockToDisplay(): FlockComplete? {
        val connection = DBConnect.getConnection()
        val flockMap = ReadFlock.allByDate(connection)

        // Get the most recent flock (sorted by date descending, take first)
        return flockMap?.entries
            ?.sortedByDescending { it.key }
            ?.firstOrNull()
            ?.value
    }
}
