package ru.ylab;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * User input processing functions.
 */
public class Input {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public static boolean isEmptyString(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String inputNonEmptyString(Scanner scanner, String prompt) {
        String value;
        do {
            System.out.print(prompt);
            value = scanner.nextLine();
        } while (isEmptyString(value));
        
        return value;
    }

    public static String inputOptionalString(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }


    public static boolean inputBoolean(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim().toLowerCase();
            if ("true".equals(value) || "false".equals(value)) {
                return Boolean.parseBoolean(value);
            } else {
                System.out.println(Console.warning("Invalid input: enter 'true' or 'false'."));
            }
        }
    }

    public static int inputPositiveInteger(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println(Console.warning(String.format("Invalid input: the number must be between %d and %d.", min, max)));
                }
            } catch (NumberFormatException e) {
                System.out.println(Console.warning("Invalid input: not an integer."));
            }
        }
    }

    public static Date inputDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String dateString = scanner.nextLine().trim();
            try {
                return DATE_FORMAT.parse(dateString);
            } catch (ParseException e) {
                System.out.println(Console.warning("Invalid input: incorrect date format."));
            }
        }
    }
}
