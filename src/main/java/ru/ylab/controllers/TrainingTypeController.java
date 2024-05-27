package ru.ylab.controllers;

import ru.ylab.app.Application;
import ru.ylab.dto.TrainingTypeDTO;
import ru.ylab.dto.UserDTO;
import ru.ylab.exceptions.ConstraintViolationException;
import ru.ylab.exceptions.ObjectExistsException;
import ru.ylab.exceptions.ObjectNotFoundException;
import ru.ylab.models.TrainingType;
import ru.ylab.services.TrainingTypeService;
import ru.ylab.utils.Console;
import ru.ylab.utils.Input;

import java.util.List;
import java.util.Scanner;


public class TrainingTypeController {
    private final Scanner scanner;
    private final TrainingTypeService trainingTypeService;

    public TrainingTypeController(Scanner scanner) {
        this.scanner = scanner;
        this.trainingTypeService = new TrainingTypeService();
    }

    public void editTrainingTypes() {
        System.out.print("\nTraining Types: ");
        String[] attributesMenu = new String[]{"View", "Add", "Edit", "Return to Main Menu"};
        MenuController.printMenu(attributesMenu);

        int action = Input.inputPositiveInteger(scanner, "Choose an action: ", 1, attributesMenu.length);
        switch (action) {
            case 1 -> viewTrainingTypes();
            case 2 -> addTrainingType();
            case 3 -> editTrainingType();
            case 4 -> System.out.println();
            default -> System.out.println(Console.warning("Invalid choice. Please try again."));
        }
    }

    public void addTrainingType() {
        UserDTO loggedInUser = Application.getUserSession().getLoggedInUser();
        if (!loggedInUser.isAdmin()) {
            System.out.println(Console.warning("Access denied."));
            return;
        }

        String trainingTypeName = Input.inputNonEmptyString(scanner, "Enter new training type: ");
        TrainingType trainingType = new TrainingType();
        trainingType.setName(trainingTypeName);
        try {
            trainingTypeService.addTrainingType(trainingType);
        } catch (ObjectExistsException e) {
            System.out.println(Console.warning("This training type already exists."));
            return;
        }

        System.out.println(Console.success("Training type added successfully."));
    }

    public void editTrainingType() {
        UserDTO loggedInUser = Application.getUserSession().getLoggedInUser();
        if (!loggedInUser.isAdmin()) {
            System.out.println(Console.warning("Access denied."));
            return;
        }

        if (!viewTrainingTypes()) {
            return;
        }
        Long trainingTypeId = (long) Input.inputPositiveInteger(scanner, "Choose training type to edit (ID): ", 1, Integer.MAX_VALUE);
        TrainingTypeDTO trainingType;
        try {
            trainingType = trainingTypeService.getTrainingTypeById(trainingTypeId);
        } catch (ObjectNotFoundException e) {
            System.out.println(Console.warning(e.getMessage()));
            return;
        }

        System.out.printf("Editing training type '%s':", trainingType.name());
        String[] attributesMenu = new String[]{"Update Name", "Delete", "Return to Main Menu"};
        MenuController.printMenu(attributesMenu);

        int action = Input.inputPositiveInteger(scanner, "Choose an action: ", 1, attributesMenu.length);
        switch (action) {
            case 1 -> editTrainingTypeName(trainingType, Input.inputNonEmptyString(scanner, "Enter new name: "));
            case 2 -> deleteTrainingType(trainingType);
            case 3 -> System.out.println();
            default -> System.out.println(Console.warning("Invalid choice. Please try again."));
        }
    }

    public void editTrainingTypeName(TrainingTypeDTO trainingType, String trainingTypeName) {
        try {
            trainingTypeService.editTrainingTypeName(trainingType, trainingTypeName);
            System.out.println(Console.success("Training type name updated successfully."));
        } catch (ObjectExistsException e) {
            System.out.println(Console.warning("Training type with this name already exists."));
        }
    }

    public void deleteTrainingType(TrainingTypeDTO trainingType) {
        try {
            if (trainingTypeService.deleteTrainingType(trainingType)) {
                System.out.println(Console.warning("Training type deleted successfully."));
            } else {
                System.out.println(Console.warning(
                        "Training type was not deleted. Probably it does not exist."
                ));
            }
        } catch (ConstraintViolationException e) {
            System.out.println(Console.warning("Training type can not be deleted because of related trainings."));
        }
    }

    public boolean viewTrainingTypes() {
        List<TrainingTypeDTO> trainingTypes;
        try {
            trainingTypes = trainingTypeService.getAllTrainingTypes();
        } catch (ObjectNotFoundException e) {
            System.out.println(e.getMessage());
            return false;
        }

        System.out.println("\nTraining Types: ");
        printTrainingTypesList(trainingTypes);
        return true;
    }

    public void printTrainingTypesList(List<TrainingTypeDTO> trainingTypes) {
        String[][] tableData = trainingTypes.stream()
            .map(trainingType -> new String[]{
                String.valueOf(trainingType.id()),
                String.valueOf(trainingType.name()),
            }).toArray(String[][]::new);

        System.out.println(Console.createTable(
            new String[]{"ID", "Name"}, tableData)
        );
    }
}
