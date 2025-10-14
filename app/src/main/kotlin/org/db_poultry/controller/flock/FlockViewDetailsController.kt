package org.db_poultry.controller.flock

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.layout.AnchorPane
import org.db_poultry.pojo.FlockPOJO.FlockComplete
import org.db_poultry.util.FlockSingleton
import org.db_poultry.util.GeneralUtil
import java.net.URL
import java.util.*

class FlockViewDetailsController : BaseFlockViewController(), Initializable {

    @FXML
    lateinit var mainAnchorPane: AnchorPane

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        initializeFlockData()
    }

    /**
     * Returns the currently selected flock from FlockSingleton for the details view.
     */
    override fun getFlockToDisplay(): FlockComplete? {
        return FlockSingleton.getCurrentFlockComplete()
    }

    @FXML
    fun backToViewFlocks() {
        GeneralUtil.navigateToMainContent(mainAnchorPane, "/fxml/content_home_flock_grid.fxml")
    }
}
