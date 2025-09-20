package org.db_poultry.controller

import org.db_poultry.App
import org.db_poultry.errors.generateErrorMessage

class DatabasePasswordUsernameController {

    fun userPasswordUsername(inputPassword: String, inputUsername: String) {
        if (!inputPassword.isBlank()) {
            App.fillDatabasePasswordUsername(inputPassword, inputUsername)
        } else {
            App.fillDatabasePasswordUsername("db_poultry", "db_poultry")
        }

    }

}