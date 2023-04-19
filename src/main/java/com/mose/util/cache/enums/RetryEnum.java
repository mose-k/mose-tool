package com.mose.util.cache.enums;

public enum RetryEnum {
    FAIL (0),
    SUCCEED (1),
    EXECUTED (2);

   int code;

    public int getCode() {
        return code;
    }

    RetryEnum(int code) {
        this.code = code;
    }
}
