package ru.ylab;

import ru.ylab.app.Application;
import ru.ylab.utils.Console;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            Application app = new Application(scanner);
            app.run();
        } catch (Exception ex) {
            System.out.println(Console.warning(ex.getMessage()));
            scanner.close();
        }
    }
}
