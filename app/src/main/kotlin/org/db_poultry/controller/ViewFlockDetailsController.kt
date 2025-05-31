package org.db_poultry.controller

import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import org.db_poultry.controller.util.flockDateSingleton

class ViewFlockDetailsController {

    @FXML
    private lateinit var dateColumn: TableColumn<Any, Any>

    @FXML
    private lateinit var depletedChickenCountColumn: TableColumn<Any, Any>

    val data = flockDateSingleton.instance

}