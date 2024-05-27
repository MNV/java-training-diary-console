package ru.ylab.services;

import ru.ylab.app.Application;
import ru.ylab.dto.TrainingDTO;
import ru.ylab.dto.UserDTO;
import ru.ylab.exceptions.ObjectExistsException;
import ru.ylab.exceptions.ObjectNotFoundException;
import ru.ylab.models.Training;
import ru.ylab.repositories.TrainingRepository;

import java.util.*;

public class TrainingService {

    private final TrainingRepository trainingRepository;

    public TrainingService() {
        this.trainingRepository = new TrainingRepository(Application.dataSource);
    }

    /**
     * Add a new training.
     *
     * @param training Training object to add.
     */
    public void addTraining(Training training) {
        Long trainingId = trainingRepository.create(training);
        Application.getLogger().log(String.format("Added a new training record with ID %d.", trainingId));
    }

    /**
     * Get the list of trainings for the logged-in user.
     *
     * @return List of trainings.
     */
    public List<TrainingDTO> getAllTrainings() {
        List<TrainingDTO> trainings;
        UserDTO loggedInUser = Application.getUserSession().getLoggedInUser();
        if (loggedInUser.isAdmin()) {
            trainings = trainingRepository.findAll();
        } else {
            trainings = trainingRepository.findAllByUserId(loggedInUser.id());
        }

        if (trainings == null || trainings.isEmpty()) {
            Application.getLogger().log("At attempt to view trainings list, but it is empty.");
            throw new ObjectNotFoundException("Trainings list is empty.");
        }

        Application.getLogger().log("Viewed trainings list.");
        return trainings;
    }

    /**
     * Get a training by its ID.
     *
     * @param trainingId Training ID.
     */
    public TrainingDTO getTrainingById(Long trainingId) {
        Optional<TrainingDTO> training = trainingRepository.read(trainingId);
        if (training.isEmpty()) {
            Application.getLogger().log(String.format("At attempt to get training object by ID %d, but it is not found.", trainingId));
            throw new ObjectNotFoundException("Training not found.");
        }

        UserDTO loggedInUser = Application.getUserSession().getLoggedInUser();
        if (loggedInUser.isAdmin() || (Objects.equals(training.get().userId(), loggedInUser.id()))) {
            Application.getLogger().log(String.format("Viewed training object by ID %d.", trainingId));
            return training.get();
        } else {
            Application.getLogger().log(String.format("An attempt was made to obtain another user's training object by ID %d.", trainingId));
            throw new ObjectNotFoundException("Training not found.");
        }
    }

    public void editTrainingType(TrainingDTO training, Long trainingTypeId) {
        if (!canAddTraining(trainingTypeId, training.date())) {
            Application.getLogger().log(
                String.format(
                    "An attempt to edit training type (from '%d', to '%d') for training with ID %d, but training with this type already exists on this day.",
                    training.trainingTypeId(), trainingTypeId, training.id()
                )
            );
            throw new ObjectExistsException("Training already exists.");
        }

        Training trainingModel = DTOToModel(training);
        trainingModel.setTrainingTypeId(trainingTypeId);
        trainingRepository.update(trainingModel);

        Application.getLogger().log(
            String.format(
                "Updated training type for training with ID %d from '%d' to '%d'.",
                training.id(), training.trainingTypeId(), trainingTypeId
            )
        );
    }

    public void editTrainingDate(TrainingDTO training, Date date) {
        Training trainingModel = DTOToModel(training);
        trainingModel.setDate(date);
        trainingRepository.update(trainingModel);

        Application.getLogger().log(
            String.format(
                "Updated training date for training with ID %d from '%s' to '%s'.",
                training.id(), training.date(), date
            )
        );
    }

    public void editTrainingDuration(TrainingDTO training, int duration) {
        Training trainingModel = DTOToModel(training);
        trainingModel.setDuration(duration);
        trainingRepository.update(trainingModel);

        Application.getLogger().log(
            String.format(
                "Updated training duration for training with ID %d from '%d' to '%d'.",
                training.id(), training.duration(), duration
            )
        );
    }

    public void editTrainingCaloriesBurned(TrainingDTO training, int calories) {
        Training trainingModel = DTOToModel(training);
        trainingModel.setCaloriesBurned(calories);
        trainingRepository.update(trainingModel);

        Application.getLogger().log(
            String.format(
                "Updated calories burned for training with ID %d from '%d' to '%d'.",
                training.id(), training.caloriesBurned(), calories
            )
        );
    }

    public void editTrainingAdditionalInfo(TrainingDTO training, String info) {
        Training trainingModel = DTOToModel(training);
        trainingModel.setAdditionalInfo(info);
        trainingRepository.update(trainingModel);

        Application.getLogger().log(
            String.format(
                "Updated additional info for training with ID %d from '%s' to '%s'.",
                training.id(), training.additionalInfo(), info
            )
        );
    }

    /**
     * Delete a training by its ID.
     *
     * @param trainingId Training ID.
     */
    public void deleteTraining(Long trainingId) {
        Optional<TrainingDTO> training = trainingRepository.read(trainingId);
        if (training.isEmpty()) {
            Application.getLogger().log(String.format("An attempt was made to delete training object by ID %d, but it was not found.", trainingId));
            throw new ObjectNotFoundException("Training not found.");
        }
        UserDTO loggedInUser = Application.getUserSession().getLoggedInUser();
        if (loggedInUser.isAdmin() || (Objects.equals(training.get().userId(), loggedInUser.id()))) {
            trainingRepository.delete(trainingId);
            Application.getLogger().log(String.format("Deleted training object by ID %d.", trainingId));
        } else {
            Application.getLogger().log(String.format("An attempt was made to delete another user's training object by ID %d.", trainingId));
            throw new ObjectNotFoundException("Training not found.");
        }
    }

    /**
     * Check if a training can be added on a specific date.
     *
     * @param trainingTypeId Type of the training.
     * @param date Date to add the training.
     */
    public boolean canAddTraining(Long trainingTypeId, Date date) {
        UserDTO currentUser = Application.getUserSession().getLoggedInUser();
        return !trainingRepository.checkIfTrainingExists(currentUser.id(), date, trainingTypeId);
    }

    private Training DTOToModel(TrainingDTO trainingDTO) {
        return new Training(
            trainingDTO.id(),
            trainingDTO.userId(),
            trainingDTO.trainingTypeId(),
            trainingDTO.date(),
            trainingDTO.duration(),
            trainingDTO.caloriesBurned(),
            trainingDTO.additionalInfo()
        );
    }
}
