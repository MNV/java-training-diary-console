package ru.ylab.controllers;


public class MenuController {
    public static final String[] MENU_AUTHENTICATED = {
        "Add Training",
        "View Trainings",
        "Edit Training",
        "Delete Training",
        "View Statistics",
        "View Audit Log",
        "Training Types",
        "Logout",
        "Exit"
    };

    public static final String[] MENU_UNAUTHENTICATED = {
        "Sign up",
        "Login",
        "Exit"
    };

    public static void printMenu(String[] options) {
        System.out.println();
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%d. %s%n", i + 1, options[i]);
        }
        System.out.println();
    }
}
