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
    private var currentSupplyComplete: SupplyComplete? = null
    private var currentSupplyType: SupplyType? = null
    private var currentSupplyID: Int = -1

    fun setCurrentSupply(supply: String) {
        currentSupplyType = ReadSupplyType.getSupplyTypeByName(getConnection(), supply)
        currentSupplyID = currentSupplyType?.supplyTypeId ?: -1
        currentSupplyComplete = if (currentSupplyID != -1) {
            ReadSupplyRecord.getLatest(getConnection(), currentSupplyID)
        } else {
            null
        }
    }

    fun getCurrentSupplyID(): Int {
        return currentSupplyID
    }

    fun getCurrentSupplyName(): String {
        return capitalizeWords(currentSupplyComplete?.supply_name)
    }

    fun getCurrentSupplyImageDir(): String? {
        // You'll need to implement image path logic based on supply name
        return currentSupplyType?.imagePath

    }

    fun getCurrentAmount(): BigDecimal {
        return currentSupplyComplete?.current ?: BigDecimal.ZERO
    }

    fun getCurrentSupplyComplete(): SupplyComplete? {
        return currentSupplyComplete
    }

    fun getSupplyUnit(): String? {
        return currentSupplyComplete?.unit
    }

    fun getSupplyAdded(): BigDecimal {
        return currentSupplyComplete?.added ?: BigDecimal.ZERO
    }

    fun getSupplyConsumed(): BigDecimal {
        return currentSupplyComplete?.consumed ?: BigDecimal.ZERO
    }

    fun getSupplyDate(): Date? {
        return currentSupplyComplete?.date
    }

    fun getUIDefaultImagePath(): String {
        return UIDefaultImagePath
    }

    fun isDefaultSupplyType(name: String): Boolean {
        return defaultSupplyNames.contains(name)
    }

    private val defaultSupplyNames = setOf(
        "Apog", "Adulticide", "String", "Fuel", "Chicken Medicine",
        "Larvicide", "Fly Glue", "Disinfectant", "Starter Feed",
        "Grower Feed", "Booster Feed", "Finisher Feed"
    )

    private fun capitalizeWords(input: String?): String =
        input?.split(" ")?.joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } ?: "BACKEND ERROR: MUST FIX"
}