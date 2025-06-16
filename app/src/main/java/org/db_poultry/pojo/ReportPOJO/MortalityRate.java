package org.db_poultry.pojo.ReportPOJO;

import java.sql.Date;

public class MortalityRate {
    private final float mortalityRate;
    private final int flockId;
    private final Date startDate;
    private final Date endDate;
    private final int startCount;
    private final int curCount;

    public MortalityRate(float mortalityRate, int flockId, Date startDate, Date endDate, int startCount, int curCount) {
        this.mortalityRate = mortalityRate;
        this.flockId = flockId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startCount = startCount;
        this.curCount = curCount;
    }

    public float getMortalityRate() {
        return mortalityRate;
    }

    public int getFlockId() {
        return flockId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getStartCount() {
        return startCount;
    }

    public int getCurCount() {
        return curCount;
    }
}
