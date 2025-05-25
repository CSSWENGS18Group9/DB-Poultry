package org.db_poultry.Controller

import org.db_poultry.Util.SceneSwitcher

import javafx.fxml.FXML
import javafx.scene.control.TableColumn

class ViewFlockDetailsController {

    @FXML
    private lateinit var dateColumn: TableColumn<Any, Any>

    @FXML
    private lateinit var depletedChickenCountColumn: TableColumn<Any, Any>

}