package org.db_poultry.util

object SupplyTypeSingleton {

    private var UIDefaultImagePath: String = "src/main/resources/img/supply-img/default.png"




    fun getUIDefaultImagePath(): String {
        return UIDefaultImagePath
    }

}