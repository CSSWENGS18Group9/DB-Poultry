package org.db_poultry.util

import java.sql.Date

object flockDetailsSingleton {

    private var id: Int = 0
    private var date: Date? = null

    fun setId(id: Int) {
        this.id = id
    }

    fun getId(): Int {
        return id
    }

    fun setDate(date: Date) {
        this.date = date
    }

    fun getDate(): Date? {
        return date
    }

}