package com.mose.util.cache.enums;

public enum MoseLockStrategyEnum {

    THROW_EXCEPTION(0),
    WAIT(1),
    RETURN_NUll(2);

//    RETURN_DEFAULT(3);

    int code;

    public int getCode() {
        return code;
    }

    MoseLockStrategyEnum(int code) {
        this.code = code;
    }
}
