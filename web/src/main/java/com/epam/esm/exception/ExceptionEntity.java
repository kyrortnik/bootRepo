package com.epam.esm.exception;

public class ExceptionEntity {

    private final String message;
    private final int code;

    public ExceptionEntity(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

}
