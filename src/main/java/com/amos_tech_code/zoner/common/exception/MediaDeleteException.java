package com.amos_tech_code.zoner.common.exception;

public class MediaDeleteException extends RuntimeException {

    public MediaDeleteException(String message) {
        super(message);
    }

    public MediaDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
