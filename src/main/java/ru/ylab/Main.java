package ru.ylab;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class Main {
    private static final String[] MENU_AUTHENTICATED = {
        "Add Training",
        "View Trainings",
        "Edit Training",
        "Delete Training",
        "View Statistics",
        "View Audit Log",
        "Logout",
        "Exit"
    };

    private static final String[] MENU_UNAUTHENTICATED = {
        "Sign up",
        "Login",
        "Exit"
    };

    private final TrainingDiary diary;
    private final Scanner scanner;

    public Main(Scanner scanner, TrainingDiary diary) {
        this.scanner = scanner;
        this.diary = diary;
    }

    public void run() {
        while (true) {
            if (diary.isAuthenticated()) {
                processMenu(MENU_AUTHENTICATED, this::processAuthenticatedChoice);
            } else {
                processMenu(MENU_UNAUTHENTICATED, this::processUnauthenticatedChoice);
            }
        }
    }

    @FunctionalInterface
    interface MenuAction {
        void execute(int choice);
    }

    private void processMenu(String[] options, MenuAction action) {
        printMenu(options);
        int choice = Input.inputPositiveInteger(scanner, "Choose an action: ", 1, options.length);
        action.execute(choice);
    }

    private void printMenu(String[] options) {
        System.out.println();
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%d. %s%n", i + 1, options[i]);
        }
    }

    private void processAuthenticatedChoice(int choice) {
        switch (choice) {
            case 1 -> addTraining();
            case 2 -> viewTrainings();
            case 3 -> editTraining();
            case 4 -> deleteTraining();
            case 5 -> viewStatistics();
            case 6 -> viewAuditLog();
            case 7 -> logout();
            case 8 -> exit();
            default -> System.out.println(Console.warning("Invalid choice. Please try again."));
        }
    }

    private void processUnauthenticatedChoice(int choice) {
        switch (choice) {
            case 1 -> registerUser();
            case 2 -> loginUser();
            case 3 -> exit();
            default -> System.out.println(Console.warning("Invalid choice. Please try again."));
        }
    }

    private void registerUser() {
        try {
            String username = Input.inputNonEmptyString(scanner, "Enter username: ");
            String password = Input.inputNonEmptyString(scanner, "Enter password: ");
            boolean isAdmin = Input.inputBoolean(scanner, "Is admin (true/false): ");
            diary.registerUser(username, password, isAdmin);
            System.out.println(Console.success("User registered successfully."));
        } catch (UserExistsException ex) {
            System.out.println(Console.warning("Username already exists."));
        }
    }

    private void loginUser() {
        String username = Input.inputNonEmptyString(scanner, "Enter username: ");
        String password = Input.inputNonEmptyString(scanner, "Enter password: ");
        if (diary.loginUser(username, password)) {
            System.out.println(Console.success("Login successful."));
        } else {
            System.out.println(Console.warning("Invalid username or password."));
        }
    }

    private void addTraining() {
        String trainingType = Input.inputNonEmptyString(scanner, "Enter training type: ");
        Date date = Input.inputDate(scanner, "Enter date (dd.MM.yyyy): ");
        if (!diary.canAddTraining(trainingType, date)) {
            System.out.println(Console.warning("You have already added this type of training on this day."));
            return;
        }
        int duration = Input.inputPositiveInteger(scanner, "Enter duration (minutes): ", 1, 10_000);
        int caloriesBurned = Input.inputPositiveInteger(scanner, "Enter calories burned: ", 1, 10_000);
        String additionalInfo = Input.inputOptionalString(scanner, "Enter additional info: ");
        Training training = new Training(trainingType, date, duration, caloriesBurned);
        training.setAdditionalInfo(additionalInfo);

        diary.addTraining(training);
        System.out.println(Console.success("Training added successfully."));
    }

    private void editTraining() {
        if (!viewTrainings()) {
            return;
        }
        int trainingId = Input.inputPositiveInteger(scanner, "Choose training to edit (ID): ", 1, Integer.MAX_VALUE);
        Training training = diary.getTrainingById(trainingId);
        if (training == null) {
            System.out.println(Console.warning("Training not found."));
            return;
        }

        System.out.printf("Editing training (ID=%d):\n", trainingId);
        String[] attributesMenu = new String[]{"Type", "Date", "Duration", "Calories Burned", "Additional Info", "Return to Main Menu"};
        printMenu(attributesMenu);
        int attribute = Input.inputPositiveInteger(scanner, "Choose an attribute to edit: ", 1, attributesMenu.length);
        switch (attribute) {
            case 1 -> diary.editTrainingType(training, Input.inputNonEmptyString(scanner, "Enter new training type: "));
            case 2 -> diary.editTrainingDate(training, Input.inputDate(scanner, "Enter new date (dd.MM.yyyy): "));
            case 3 -> diary.editTrainingDuration(training, Input.inputPositiveInteger(scanner, "Enter new duration (minutes): ", 1, 10_000));
            case 4 -> diary.editTrainingCaloriesBurned(training, Input.inputPositiveInteger(scanner, "Enter new calories burned: ", 1, 10_000));
            case 5 -> diary.editTrainingAdditionalInfo(training, Input.inputOptionalString(scanner, "Enter new additional info: "));
            case 6 -> System.out.println();
            default -> System.out.println(Console.warning("Invalid choice. Please try again."));
        }
        System.out.println(Console.success("Training updated successfully."));
    }

    private boolean viewTrainings() {
        List<Training> trainings = diary.getTrainings();
        if (trainings.isEmpty()) {
            System.out.println("Trainings list is empty.");
            return false;
        }
        System.out.println("\nTrainings List (sorted by Date descending): ");
        printTrainingsList(trainings);
        return true;
    }

    private void deleteTraining() {
        if (!viewTrainings()) {
            return;
        }
        int trainingId = Input.inputPositiveInteger(scanner, "Choose training to delete (ID): ", 1, Integer.MAX_VALUE);
        if (diary.deleteTraining(trainingId)) {
            System.out.println(Console.success("Training deleted successfully."));
        } else {
            System.out.println(Console.warning("Training not found."));
        }
    }

    private void viewStatistics() {
        Statistics statistics = diary.getStatistics();
        System.out.println(statistics);
    }

    private void viewAuditLog() {
        try {
            List<String> auditLog = diary.getAuditLog();
            System.out.println("Audit log: ");
            auditLog.forEach(System.out::println);
            System.out.println();
        } catch (AccessDeniedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void logout() {
        diary.logout();
    }

    private void exit() {
        scanner.close();
        System.exit(0);
    }

    private void printTrainingsList(List<Training> trainings) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String[][] tableData = trainings.stream()
                .map(training -> new String[]{
                        String.valueOf(training.getId()),
                        dateFormat.format(training.getDate()),
                        training.getTrainingType(),
                        String.valueOf(training.getDuration()),
                        String.valueOf(training.getCaloriesBurned()),
                        training.getAdditionalInfo(),
                        training.getUsername()
                }).toArray(String[][]::new);

        System.out.println(Console.createTable(
            new String[]{"ID", "Date", "Type", "Duration (min)", "Calories Burned", "Additional Info", "User"}, tableData)
        );
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TrainingDiary diary = new TrainingDiary();
        Main app = new Main(scanner, diary);
        app.run();
    }
}
