package ru.ylab.app;

import lombok.Getter;
import ru.ylab.auth.UserSession;
import ru.ylab.controllers.*;
import ru.ylab.integrations.DataSource;
import ru.ylab.utils.Console;
import ru.ylab.utils.Input;

import java.util.Scanner;


public class Application {
    public static DataSource dataSource = new DataSource();
    @Getter
    private static final UserSession userSession = new UserSession();
    @Getter
    private static final Logger logger = new Logger();

    private final Scanner scanner;
    private final UserController userController;
    private final TrainingController trainingController;
    private final TrainingTypeController trainingTypeController;
    private final StatisticsController statisticsController;
    private final AuditLogController auditLogController;

    public Application(Scanner scanner) {
        this.scanner = scanner;
        this.userController = new UserController(scanner);
        this.trainingController = new TrainingController(scanner);
        this.trainingTypeController = new TrainingTypeController(scanner);
        this.statisticsController = new StatisticsController();
        this.auditLogController = new AuditLogController();
    }

    public void run() {
        while (true) {
            if (userSession.isAuthenticated()) {
                processMenu(MenuController.MENU_AUTHENTICATED, this::processAuthenticatedChoice);
            } else {
                processMenu(MenuController.MENU_UNAUTHENTICATED, this::processUnauthenticatedChoice);
            }
        }
    }

    public void processMenu(String[] options, MenuAction action) {
        MenuController.printMenu(options);
        int choice = Input.inputPositiveInteger(scanner, "Choose an action: ", 1, options.length);
        action.execute(choice);
    }

    private void processUnauthenticatedChoice(int choice) {
        switch (choice) {
            case 1 -> userController.register();
            case 2 -> userController.login();
            case 3 -> exit();
            default -> handleInvalidChoice();
        }
    }

    private void processAuthenticatedChoice(int choice) {
        switch (choice) {
            case 1 -> trainingController.addTraining();
            case 2 -> trainingController.viewTrainings();
            case 3 -> trainingController.editTraining();
            case 4 -> trainingController.deleteTraining();
            case 5 -> statisticsController.viewStatistics();
            case 6 -> auditLogController.viewAuditLog();
            case 7 -> trainingTypeController.editTrainingTypes();
            case 8 -> userController.logout();
            case 9 -> exit();
            default -> handleInvalidChoice();
        }
    }

    private void handleInvalidChoice() {
        System.out.println(Console.warning("Invalid choice. Please try again."));
    }

    private void exit() {
        scanner.close();
        System.exit(0);
    }
}
