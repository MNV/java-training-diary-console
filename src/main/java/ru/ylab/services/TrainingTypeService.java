package ru.ylab.services;

import ru.ylab.app.Application;
import ru.ylab.dto.TrainingTypeDTO;
import ru.ylab.exceptions.ObjectExistsException;
import ru.ylab.exceptions.ObjectNotFoundException;
import ru.ylab.models.TrainingType;
import ru.ylab.repositories.TrainingTypeRepository;

import java.util.List;
import java.util.Optional;

public class TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeService() {
        this.trainingTypeRepository = new TrainingTypeRepository(Application.dataSource);
    }

    public void addTrainingType(TrainingType trainingType) {
        if (!canAddTrainingType(trainingType.getName())) {
            Application.getLogger().log("An attempt to add an existing training type.");
            throw new ObjectExistsException("This training type already exists.");
        }

        trainingTypeRepository.create(trainingType);
        Application.getLogger().log(String.format("Added new training type '%s'.", trainingType.getName()));
    }

    public List<TrainingTypeDTO> getAllTrainingTypes() {
        List<TrainingTypeDTO> trainingTypes = trainingTypeRepository.findAll();
        if (trainingTypes == null || trainingTypes.isEmpty()) {
            Application.getLogger().log("An attempt to get training types list.");
            throw new ObjectNotFoundException("Training types list is empty.");
        }

        Application.getLogger().log("Viewed training types list.");
        return trainingTypes;
    }

    public TrainingTypeDTO getTrainingTypeById(Long trainingTypeId) {
        Optional<TrainingTypeDTO> trainingType = trainingTypeRepository.read(trainingTypeId);
        if (trainingType.isEmpty()) {
            Application.getLogger().log(String.format("An attempt to get training type by ID %d.", trainingTypeId));
            throw new ObjectNotFoundException("Training type not found.");
        }

        Application.getLogger().log(String.format("Viewed training type by ID %d.", trainingTypeId));

        return trainingType.get();
    }

    public void editTrainingTypeName(TrainingTypeDTO trainingType, String trainingTypeName) {
        if (!canAddTrainingType(trainingTypeName)) {
            Application.getLogger().log("An attempt to edit training type name, but training type already exists.");
            throw new ObjectExistsException("This training type already exists.");
        }

        TrainingType trainingTypeModel = DTOToModel(trainingType);
        trainingTypeModel.setName(trainingTypeName);
        trainingTypeRepository.update(trainingTypeModel);

        Application.getLogger().log(String.format("Training type name edited from '%s' to '%s'.", trainingType.name(), trainingTypeName));
    }

    public boolean deleteTrainingType(TrainingTypeDTO trainingType) {
        if (trainingTypeRepository.delete(trainingType.id()) > 0) {
            Application.getLogger().log(String.format("Deleted training type '%s'.", trainingType.name()));
            return true;
        } else {
            Application.getLogger().log("An attempt to delete training type, but no rows affected.");
            return false;
        }
    }

    public boolean canAddTrainingType(String trainingTypeName) {
        return !trainingTypeRepository.checkIfTrainingTypeExists(trainingTypeName);
    }

    private TrainingType DTOToModel(TrainingTypeDTO trainingTypeDTO) {
        return new TrainingType(
            trainingTypeDTO.id(),
            trainingTypeDTO.name()
        );
    }
}
