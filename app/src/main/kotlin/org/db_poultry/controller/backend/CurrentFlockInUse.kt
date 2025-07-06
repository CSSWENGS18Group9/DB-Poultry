package org.db_poultry.controller.backend

class CurrentFlockInUse {
    // This class can be used to manage the current flock in use, mainly for flock view
    companion object {
        private var currentFlockDate: String? = null

        fun setCurrentFlockDate(date: String) {
            currentFlockDate = date
        }

        fun getCurrentFlockDate(): String? {
            return currentFlockDate
        }
    }


}