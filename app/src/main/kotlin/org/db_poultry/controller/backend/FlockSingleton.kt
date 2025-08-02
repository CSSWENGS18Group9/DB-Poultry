package org.db_poultry.controller.backend

import org.db_poultry.pojo.FlockPOJO.FlockComplete
import org.db_poultry.pojo.FlockPOJO.FlockDetails
import java.sql.Date

// This object can be used to manage the current flock in use, mainly for flock view
object FlockSingleton {

    private var currentFlockFXML: String? = null
    private var currentFlockComplete: FlockComplete? = null

    fun setCurrentFlockFXML(fxml: String) {
        currentFlockFXML = fxml
    }

    fun getCurrentFlockFXML(): String? {
        return currentFlockFXML
    }

    fun setCurrentFlockComplete(flock: FlockComplete) {
        currentFlockComplete = flock
    }

    fun getCurrentFlockComplete(): FlockComplete? {
        return currentFlockComplete
    }

    fun getCurrentFlockID(): Int {
        return currentFlockComplete?.flock?.flockId ?: -1
    }

    fun getCurrentStartingCount(): Int {
        return currentFlockComplete?.flock?.startingCount ?: 0
    }

    fun getCurrentFlockDate(): Date? {
        return currentFlockComplete?.flock?.startingDate
    }

    fun getCurrentCount(): Int {
        val startingCount = getCurrentStartingCount()
        val depletedCount = currentFlockComplete?.flockDetails?.sumOf { it.depletedCount } ?: 0
        return startingCount - depletedCount
    }

    fun getTotalDepletedCount(): Int {
        return currentFlockComplete?.flockDetails?.sumOf { it.depletedCount } ?: 0
    }

    fun getFlockDetails(): List<FlockDetails> {
        return currentFlockComplete?.flockDetails ?: emptyList()
    }
}