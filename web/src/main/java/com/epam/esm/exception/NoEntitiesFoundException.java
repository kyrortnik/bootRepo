package com.epam.esm.exception;

public class NoEntitiesFoundException extends RuntimeException {

    public NoEntitiesFoundException() {
    }

    public NoEntitiesFoundException(String message) {
        super(message);
    }
}
