package ru.ylab.exceptions;

public class ConstraintViolationException extends RuntimeException {
    public ConstraintViolationException(String errorMessage) {
        super(errorMessage);
    }
}
