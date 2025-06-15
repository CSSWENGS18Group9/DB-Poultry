package org.db_poultry.controller

import org.db_poultry.util.SceneSwitcher
import org.db_poultry.util.GeneralUtil

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.text.Text

class ViewSuppliesController {

    @FXML
    private lateinit var currAmountCol: TableColumn<Any, Any>

    @FXML
    private lateinit var currDateLbl: Text

    @FXML
    private lateinit var currHistoryBtn: Button

    @FXML
    private lateinit var dateHistoryBtn: Button

    @FXML
    private lateinit var qtyAddedCol: TableColumn<Any, Any>

    @FXML
    private lateinit var qtyDepletedCol: TableColumn<Any, Any>

    @FXML
    private lateinit var supplyTypeCol: TableColumn<Any, Any>

    @FXML
    private lateinit var viewSuppliesLbl: Text

    @FXML
    private lateinit var viewSuppliesTable: TableView<Any>

}