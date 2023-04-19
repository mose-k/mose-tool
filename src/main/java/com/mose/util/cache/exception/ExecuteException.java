package com.mose.util.cache.exception;

public class ExecuteException extends RuntimeException {

    public ExecuteException() {
    }

    public ExecuteException(String message) {
        super(message);
    }

    public ExecuteException(Throwable cause) {
        super(cause.getMessage(),cause);
        this.setStackTrace(cause.getStackTrace());
    }
}
