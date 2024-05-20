package ru.ylab;

public class UserExistsException extends Exception {
    public UserExistsException(String errorMessage) {
        super(errorMessage);
    }
}
