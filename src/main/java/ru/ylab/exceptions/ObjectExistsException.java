package ru.ylab.exceptions;

public class ObjectExistsException extends RuntimeException {
    public ObjectExistsException(String errorMessage) {
        super(errorMessage);
    }
}

