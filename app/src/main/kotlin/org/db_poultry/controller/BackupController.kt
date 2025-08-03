package org.db_poultry.controller

import org.db_poultry.theLifesaver.Variables

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import org.kordamp.ikonli.javafx.FontIcon
import java.io.File
import java.net.URL
import java.util.ResourceBundle

class BackupController : Initializable {

    @FXML
    private lateinit var backupAnchorPane: AnchorPane

    @FXML
    private lateinit var backupDatesComboBox: ComboBox<String>

    @FXML
    private lateinit var closeButton: FontIcon

    @FXML
    private lateinit var passPasswordField: PasswordField

    @FXML
    private lateinit var passTextField: TextField

    @FXML
    private lateinit var showPassButton: Button

    private val backupDirectory: String
        get() = Variables.getBackupFolderPath()

    private var isPasswordShown = false

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        setupComboBox()
        setupPassword()
    }

    private fun setupComboBox() {
        val dir = File(backupDirectory)
        val files = dir.listFiles()?.filter { it.isFile }?.map { it.name }?.sortedDescending() ?: emptyList()
        backupDatesComboBox.items.setAll(files)
    }

    // TODO: Add to main login @Dattebayo25
    private fun setupPassword() {
        passTextField.isVisible = false

        showPassButton.setOnAction {
            if (!isPasswordShown) {
                passTextField.text = passPasswordField.text
                passPasswordField.isVisible = false
                passTextField.isVisible = true
            } else {
                passPasswordField.text = passTextField.text
                passTextField.isVisible = false
                passPasswordField.isVisible = true
            }
            isPasswordShown = !isPasswordShown
        }
    }

    @FXML
    fun confirm() {


        closePopup()
    }

    @FXML
    fun closePopup() {
        val stage = backupAnchorPane.scene.window as Stage
        stage.close()
    }
}