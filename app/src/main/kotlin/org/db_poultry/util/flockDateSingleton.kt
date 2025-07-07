package org.db_poultry.util

import java.sql.Date

object flockDateSingleton {

    private var date: Date? = null

    fun setDate(date: Date?) {
        this.date = date
    }

    fun getDate(): Date? {
        return date
    }
}