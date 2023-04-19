package com.mose.util.cache.enums;

import java.util.concurrent.TimeUnit;

public enum MoseCacheTimeEnum {

    THREE_MINUTES(3,TimeUnit.MINUTES),
    FIVE_MINUTES(5,TimeUnit.MINUTES),
    HALF_HOUR(30,TimeUnit.MINUTES),
    HOUR(1,TimeUnit.MINUTES),
    DAY(1,TimeUnit.DAYS),
    Month(30,TimeUnit.DAYS);

    long time;
    TimeUnit timeUnit;

    public long getTime() {
        return time;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    MoseCacheTimeEnum(long time, TimeUnit timeUnit) {
        this.time = time;
        this.timeUnit = timeUnit;
    }
}
