package org.db_poultry.util

import org.db_poultry.db.supplyRecordDAO.ReadSupplyRecord
import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyTypeDAO.ReadSupplyType
import org.db_poultry.pojo.SupplyPOJO.SupplyComplete
import org.db_poultry.pojo.SupplyPOJO.SupplyType

import java.math.BigDecimal
import java.sql.Date

// This object can be used to manage the current supply in use, mainly for supply grid
object SupplySingleton {

    private var UIDefaultImagePath: String = "src/main/resources/img/supply-img/default.png"
    private var latestSupplyComplete: SupplyComplete? = null
    private var currentSupplyType: SupplyType? = null
    private var currentSupplyID: Int = -1

    fun setCurrentSupply(supply: String) {
        currentSupplyType = ReadSupplyType.getSupplyTypeByName(getConnection(), supply)
        currentSupplyID = currentSupplyType?.supplyTypeId ?: -1
        latestSupplyComplete = if (currentSupplyID != -1) {
            ReadSupplyRecord.getLatest(getConnection(), currentSupplyID)
        } else {
            null
        }
    }

    fun getCurrentSupplyID(): Int {
        return currentSupplyID
    }

    fun getCurrentSupplyName(): String {
        return currentSupplyType?.name ?: "Unknown Supply"
    }

    fun getCurrentSupplyImageDir(): String {
        // You'll need to implement image path logic based on supply name
        return currentSupplyType?.imagePath?: UIDefaultImagePath

    }

    fun getCurrentAmount(): BigDecimal {
        return latestSupplyComplete?.current ?: BigDecimal.ZERO
    }

    fun getCurrentSupplyComplete(): SupplyComplete? {
        return latestSupplyComplete
    }

    fun getSupplyUnit(): String? {
        return currentSupplyType?.unit
    }

    fun getSupplyAdded(): BigDecimal {
        return latestSupplyComplete?.added ?: BigDecimal.ZERO
    }

    fun getSupplyConsumed(): BigDecimal {
        return latestSupplyComplete?.consumed ?: BigDecimal.ZERO
    }

    fun getSupplyDate(): Date? {
        return latestSupplyComplete?.date
    }

    fun getUIDefaultImagePath(): String {
        return UIDefaultImagePath
    }

    fun isDefaultSupplyType(name: String): Boolean {
        return defaultSupplyNames.contains(name)
    }

    private val defaultSupplyNames = setOf(
        "apog", "adulticide", "string", "fuel", "chicken medicine",
        "larvicide", "fly glue", "disinfectant", "starter feed",
        "grower feed", "booster feed", "finisher feed"
    )
}