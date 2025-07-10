package org.db_poultry.controller.backend

import org.db_poultry.pojo.FlockPOJO.FlockComplete

class CurrentFlockInUse {
    // This class can be used to manage the current flock in use, mainly for flock view
    companion object {
        private var currentFlockFXML: String? = null
        private var currentFlock: FlockComplete? = null

        fun setCurrentFlockFXML(fxml: String) {
            currentFlockFXML = fxml
        }

        fun getCurrentFlockFXML(): String? {
            return currentFlockFXML
        }

        fun setCurrentFlockComplete(flock: FlockComplete) {
            currentFlock = flock
        }

        fun getCurrentFlockComplete(): FlockComplete? {
            return currentFlock
        }
    }


}