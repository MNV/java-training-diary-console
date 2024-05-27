package ru.ylab.exceptions;

public class DatabaseConfigurationException extends RuntimeException {
    public DatabaseConfigurationException(String errorMessage) {
        super(errorMessage);
    }
}
