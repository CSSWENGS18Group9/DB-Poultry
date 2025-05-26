package org.db_poultry.controller

import org.db_poultry.db.flockDAO.CreateFlock
import org.db_poultry.db.flockDAO.ReadFlock
import org.db_poultry.errors.generateErrorMessage
import java.sql.Connection
import java.sql.Date

fun recordFlockDetails(connection: Connection?, startCount: Int, startDate: Date?) {
    val flocks = validateAndReadAllByDates(connection)
    if (flocks != null){

    }
}

