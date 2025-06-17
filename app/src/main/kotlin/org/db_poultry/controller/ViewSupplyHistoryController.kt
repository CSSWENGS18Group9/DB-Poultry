package org.db_poultry.controller

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView

class ViewSupplyHistoryController {

    @FXML
    private lateinit var currAmountCol: TableColumn<Any, Any>

    @FXML
    private lateinit var currDateLbl: Label

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
    private lateinit var viewSuppliesLbl: Label

    @FXML
    private lateinit var viewSuppliesTable: TableView<Any>

}