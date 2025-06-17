package org.db_poultry.controller

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.AnchorPane

class ViewDateHistoryController {

    @FXML
    private lateinit var currAmountCol: TableColumn<Any, Any>

    @FXML
    private lateinit var currDateLbl: Label

    @FXML
    private lateinit var currHistoryBtn: Button

    @FXML
    private lateinit var qtyAddedCol: TableColumn<Any, Any>

    @FXML
    private lateinit var qtyDepletedCol: TableColumn<Any, Any>

    @FXML
    private lateinit var supplyHistoryBtn: Button

    @FXML
    private lateinit var supplyTypeCol: TableColumn<Any, Any>

    @FXML
    private lateinit var viewSuppliesLbl: Label

    @FXML
    private lateinit var viewSuppliesTable: TableView<Any>

    @FXML
    private lateinit var viewSupplyAnchorPane: AnchorPane

    @FXML
    fun switchToViewSupplyHistory(event: ActionEvent) {

    }

    @FXML
    fun switchToViewCurrentHistory(event: ActionEvent) {

    }

}
