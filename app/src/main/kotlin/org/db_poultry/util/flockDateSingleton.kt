package org.db_poultry.controller.util

import java.sql.Date

object flockDateSingleton {

    val instance = flockDateSingleton

    private var date: Date? = null

    fun setDate(date: Date?) {
        this.date = date
    }

    fun getDate(): Date? {
        return date
    }
}