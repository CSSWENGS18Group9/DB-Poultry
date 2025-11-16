package org.db_poultry.controller

import org.db_poultry.App

object DatabasePasswordNameController {
    /**
     * Called only on first run to set up database credentials
     */
    fun setupDatabaseCredentials(inputPassword: String, inputName: String) {
        if (!inputPassword.isBlank()) {
            App.fillDatabasePasswordName(inputPassword, inputName)
        } else {
            App.fillDatabasePasswordName("db_poultry", "db_poultry")
        }

        App.initializeApplication()
    }
}