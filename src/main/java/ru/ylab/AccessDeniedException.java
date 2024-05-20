package ru.ylab;

public class AccessDeniedException extends Exception {
    public AccessDeniedException(String errorMessage) {
        super(errorMessage);
    }
}
