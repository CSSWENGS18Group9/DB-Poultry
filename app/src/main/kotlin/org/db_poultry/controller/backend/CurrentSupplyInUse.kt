package org.db_poultry.controller.backend

class CurrentSupplyInUse {
    companion object {
        // This class can be used to manage the current supply in use
        // For example, it can hold the current supply type or record being processed
        // It can also provide methods to get/set the current supply in use
        private var currentSupply: String? = null
        private var currentSupplyImageDir: String? = null

        fun setCurrentSupply(supply: String) {
            currentSupply = supply

        }

        fun setCurrentSupplyImageDir(imageDir: String) {
            currentSupplyImageDir = imageDir
        }

        fun getCurrentSupply(): String? {
            return currentSupply
        }

        fun getCurrentSupplyImageDir(): String? {
            return currentSupplyImageDir
        }
    }
}