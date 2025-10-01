package org.db_poultry.controller

import org.db_poultry.App

class DatabasePasswordNameController {

    fun userPasswordName(inputPassword: String, inputName: String) {
        if (!inputPassword.isBlank()) {
            App.fillDatabasePasswordName(inputPassword, inputName)
        } else {
            App.fillDatabasePasswordName("db_poultry", "db_poultry")
        }

    }

}