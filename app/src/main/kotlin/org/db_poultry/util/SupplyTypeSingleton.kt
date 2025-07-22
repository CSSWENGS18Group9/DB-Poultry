package org.db_poultry.util

import org.db_poultry.db.supplyRecordDAO.ReadSupplyRecord
import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyTypeDAO.ReadSupplyType

import java.math.BigDecimal

object SupplyTypeSingleton {

    private var UIDefaultImagePath: String = "src/main/resources/img/supply-img/default.png"

    // This class can be used to manage the current supply in use, mainly for supply grid
    private var currentSupply: String? = null
    private var currentSupplyImageDir: String? = null
    private var currentSupplyID: Int = -1
    private var currentAmount: BigDecimal = BigDecimal.ZERO


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

    fun getCurrentAmount(): BigDecimal {
        currentSupplyID = ReadSupplyType.getSupplyTypeByName(getConnection(), currentSupply).supplyTypeId

        val supplyComplete = ReadSupplyRecord.getLatest(getConnection(), currentSupplyID)
        return supplyComplete?.current ?: BigDecimal.ZERO
    }

    private val defaultSupplyNames = setOf(
        "Apog",
        "Adulticide",
        "String",
        "Fuel",
        "Chicken Medicine",
        "Larvicide",
        "Fly Glue",
        "Disinfectant",
        "Starter Feed",
        "Grower Feed",
        "Booster Feed",
        "Finisher Feed"
    )

    fun getUIDefaultImagePath(): String {
        return UIDefaultImagePath
    }

    fun isDefaultSupplyType(name: String): Boolean {
        return defaultSupplyNames.contains(name)
    }

}