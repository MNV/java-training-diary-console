package ru.ylab.controllers;

import ru.ylab.utils.Console;
import ru.ylab.utils.Input;
import ru.ylab.exceptions.ObjectExistsException;
import ru.ylab.services.UserService;

import java.util.Scanner;

public class UserController {
    private final Scanner scanner;
    private final UserService userService;

    public UserController(Scanner scanner) {
        this.scanner = scanner;
        this.userService = new UserService();
    }
    public void register() {
        try {
            String username = Input.inputNonEmptyString(scanner, "Enter username: ");
            String password = Input.inputNonEmptyString(scanner, "Enter password: ");
            boolean isAdmin = Input.inputBoolean(scanner, "Is admin (true/false): ");
            userService.registerUser(username, password, isAdmin);
            System.out.println(Console.success("User registered successfully."));
        } catch (ObjectExistsException ex) {
            System.out.println(Console.warning("Username already exists."));
        }
    }

    public void login() {
        String username = Input.inputNonEmptyString(scanner, "Enter username: ");
        String password = Input.inputNonEmptyString(scanner, "Enter password: ");
        if (userService.loginUser(username, password)) {
            System.out.println(Console.success("Login successful."));
        } else {
            System.out.println(Console.warning("Invalid username or password."));
        }
    }

    public void logout() {
        userService.logout();
    }
}
