package org.db_poultry.controller

import org.db_poultry.App
import org.db_poultry.errors.generateErrorMessage

class DatabasePasswordController {

    fun userPassword(inputPassword: String) {
        if (!inputPassword.isBlank()) {
            App.fillDatabasePassword(inputPassword)
        } else {
            generateErrorMessage(
                "Error at `userPassword()` in `DatabasePasswordController.kt`",
                "Password is blank.",
                "Password must not be blank."
            )
        }

    }

}