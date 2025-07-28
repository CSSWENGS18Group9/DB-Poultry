package org.db_poultry.util

import org.db_poultry.db.flockDAO.DeleteFlock.undoCreateFlock
import org.db_poultry.db.flockDetailsDAO.DeleteFlockDetail.getFlockDetailsToDelete
import org.db_poultry.db.supplyRecordDAO.DeleteSupplyRecord.undoCreateSupplyRecord
import org.db_poultry.db.supplyTypeDAO.DeleteSupplyType.undoCreateSupplyType
import java.sql.Connection

object undoSingleton {

    /**
     * 1 = undo Flock
     * 2 = undo Flock Detail
     * 3 = undo Supply Record
     * 4 = undo Supply Type
     */
    private var undoMode = 0

    // For multiple undo actions
    private var isFeedRetrieval = false

    /**
     * Undo button will use this function
     */
    fun undo(conn: Connection?) {
        when (undoMode) {
            1 -> undoCreateFlock(conn)
            2 -> getFlockDetailsToDelete(conn)
            3 -> undoCreateSupplyRecord(conn)
            4 -> undoCreateSupplyType(conn)
        }
        undoMode = 0 // reset once done
    }

    /**
     * Call this function whenever a Create action is done
     *
     * Use the undoTypes object when calling
     * e.g. setUndoMode(undoTypes.doUndoFlock)
     */
    fun setUndoMode(undoMode: Int) {
        this.undoMode = undoMode
    }

    fun setIsFeedRetrieval(isFeedRetrieval: Boolean) {
        this.isFeedRetrieval = isFeedRetrieval
    }

    fun getIsFeedRetrieval(): Boolean {
        return isFeedRetrieval
    }

}