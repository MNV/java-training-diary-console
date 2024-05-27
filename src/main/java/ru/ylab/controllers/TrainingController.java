package ru.ylab.controllers;

import ru.ylab.app.Application;
import ru.ylab.dto.TrainingDTO;
import ru.ylab.dto.UserDTO;
import ru.ylab.exceptions.ObjectExistsException;
import ru.ylab.exceptions.ObjectNotFoundException;
import ru.ylab.models.Training;
import ru.ylab.services.TrainingService;
import ru.ylab.services.TrainingTypeService;
import ru.ylab.utils.Console;
import ru.ylab.utils.Input;

import java.text.SimpleDateFormat;
import java.util.*;


public class TrainingController {
    private final Scanner scanner;
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;
    private final TrainingTypeController trainingTypeController;

    public TrainingController(Scanner scanner) {
        this.scanner = scanner;
        this.trainingService = new TrainingService();
        this.trainingTypeService = new TrainingTypeService();
        this.trainingTypeController = new TrainingTypeController(scanner);
    }

    public void addTraining() {
        if (!trainingTypeController.viewTrainingTypes()) {
            System.out.println(Console.warning("No trainings types available. It is not possible to add a new training."));
            return;
        }
        Long trainingTypeId = (long) Input.inputPositiveInteger(scanner, "Enter training type (ID): ", 1, Integer.MAX_VALUE);
        try {
            trainingTypeService.getTrainingTypeById(trainingTypeId);
        } catch (ObjectNotFoundException e) {
            System.out.println(Console.warning(e.getMessage()));
            return;
        }
        Date date = Input.inputDate(scanner, "Enter date (dd.MM.yyyy): ");
        if (!trainingService.canAddTraining(trainingTypeId, date)) {
            System.out.println(Console.warning("You have already added this type of training on this day."));
            return;
        }
        int duration = Input.inputPositiveInteger(scanner, "Enter duration (minutes): ", 1, 10_000);
        int caloriesBurned = Input.inputPositiveInteger(scanner, "Enter calories burned: ", 1, 10_000);
        String additionalInfo = Input.inputOptionalString(scanner, "Enter additional info: ");

        UserDTO loggedInUser = Application.getUserSession().getLoggedInUser();
        Training training = new Training();
        training.setUserId(loggedInUser.id());
        training.setTrainingTypeId(trainingTypeId);
        training.setDate(date);
        training.setDuration(duration);
        training.setCaloriesBurned(caloriesBurned);
        training.setAdditionalInfo(additionalInfo);

        trainingService.addTraining(training);
        System.out.println(Console.success("Training added successfully."));
    }

    public void editTraining() {
        if (!viewTrainings()) {
            return;
        }
        Long trainingId = (long) Input.inputPositiveInteger(scanner, "Choose a training to edit (ID): ", 1, Integer.MAX_VALUE);
        TrainingDTO training;
        try {
            training = trainingService.getTrainingById(trainingId);
        } catch (ObjectNotFoundException e) {
            System.out.println(Console.warning(e.getMessage()));
            return;
        }

        System.out.printf("Editing training (ID=%d):", trainingId);
        String[] attributesMenu = new String[]{"Type", "Date", "Duration", "Calories Burned", "Additional Info", "Return to Main Menu"};
        MenuController.printMenu(attributesMenu);

        int attribute = Input.inputPositiveInteger(scanner, "Choose an attribute to edit: ", 1, attributesMenu.length);
        switch (attribute) {
            case 1 -> editTrainingType(training);
            case 2 -> trainingService.editTrainingDate(training, Input.inputDate(scanner, "Enter new date (dd.MM.yyyy): "));
            case 3 -> trainingService.editTrainingDuration(training, Input.inputPositiveInteger(scanner, "Enter new duration (minutes): ", 1, 10_000));
            case 4 -> trainingService.editTrainingCaloriesBurned(training, Input.inputPositiveInteger(scanner, "Enter new calories burned: ", 1, 10_000));
            case 5 -> trainingService.editTrainingAdditionalInfo(training, Input.inputOptionalString(scanner, "Enter new additional info: "));
            case 6 -> System.out.println();
            default -> System.out.println(Console.warning("Invalid choice. Please try again."));
        }
    }

    public void editTrainingType(TrainingDTO training) {
        if (!trainingTypeController.viewTrainingTypes()) {
            System.out.println(Console.warning("No training types available. It is not possible to edit a training."));
            return;
        }
        Long trainingTypeId = (long) Input.inputPositiveInteger(
            scanner, "Enter new training type (ID): ", 1, Integer.MAX_VALUE
        );
        try {
            trainingService.editTrainingType(training, trainingTypeId);
            System.out.println(Console.success("Training type updated successfully."));
        } catch (ObjectExistsException e) {
            System.out.println(Console.warning("You have already added this type of training on this day."));
        }
    }

    public boolean viewTrainings() {
        List<TrainingDTO> trainings;
        try {
            trainings = trainingService.getAllTrainings();
        } catch (ObjectNotFoundException e) {
            System.out.println(e.getMessage());
            return false;
        }

        System.out.println("\nTrainings List (sorted by Date descending): ");
        printTrainingsList(trainings);
        return true;
    }

    public void deleteTraining() {
        if (!viewTrainings()) {
            return;
        }
        Long trainingId = (long) Input.inputPositiveInteger(
            scanner, "Choose training to delete (ID): ", 1, Integer.MAX_VALUE
        );
        try {
            trainingService.deleteTraining(trainingId);
            System.out.println(Console.success("Training deleted successfully."));
        } catch (ObjectNotFoundException e) {
            System.out.println(Console.warning(e.getMessage()));
        }
    }

    public void printTrainingsList(List<TrainingDTO> trainings) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String[][] tableData = trainings.stream()
            .map(training -> new String[]{
                String.valueOf(training.id()),
                dateFormat.format(training.date()),
                training.trainingType(),
                String.valueOf(training.duration()),
                String.valueOf(training.caloriesBurned()),
                training.additionalInfo(),
                training.username()
            }).toArray(String[][]::new);

        System.out.println(
            Console.createTable(
                new String[]{"ID", "Date", "Type", "Duration (min)", "Calories Burned", "Additional Info", "User"},
                tableData
            )
        );
    }
}
