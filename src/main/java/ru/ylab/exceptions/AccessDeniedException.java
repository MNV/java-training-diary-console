package ru.ylab.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String errorMessage) {
        super(errorMessage);
    }
}

