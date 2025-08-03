package org.db_poultry.controller

import org.db_poultry.theLifesaver.Variables
import org.db_poultry.App

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
import org.db_poultry.theLifesaver.Backup
import org.db_poultry.util.GeneralUtil
import org.kordamp.ikonli.javafx.FontIcon
import java.io.File
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
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

    fun extractFormattedDate(filename: String): String? {
        val regex = Regex("""dbp_backup_([A-Za-z]+-\d{2}-\d{4})\.sql""")
        val match = regex.find(filename) ?: return null
        val datePart = match.groupValues[1]
        val inputFormatter = DateTimeFormatter.ofPattern("MMMM-dd-yyyy", Locale.ENGLISH)
        val date = LocalDate.parse(datePart, inputFormatter)
        return date.format(inputFormatter)
    }

    @FXML
    fun confirm() {
        val app = App()
        val filename = backupDatesComboBox.value
        val stringDate = extractFormattedDate(filename)

//        Backup.TL_restoreDatabase(
//            stringDate,
//            app.databaseName,
//            app.databasePass
//        )

        println("Restoring database from backup: $filename")
        closePopup()

        GeneralUtil.loadContentView(GeneralUtil.getMainContentPane()!!, "/fxml/content_home.fxml")
    }

    @FXML
    fun closePopup() {
        val stage = backupAnchorPane.scene.window as Stage
        stage.close()
    }
}