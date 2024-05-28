package services;

import extensions.TestDatabaseExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.ylab.app.Application;
import ru.ylab.dto.TrainingDTO;
import ru.ylab.exceptions.ObjectNotFoundException;
import ru.ylab.models.Training;
import ru.ylab.services.TrainingService;
import ru.ylab.services.UserService;

import java.util.Date;
import java.util.List;

import static fixtures.DatabaseFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TestDatabaseExtension.class)
class TrainingServiceTest {

    TrainingService trainingService;
    UserService userService;

    @BeforeEach
    public void setUp() {
        Application.dataSource = TestDatabaseExtension.DATA_SOURCE;
        trainingService = new TrainingService();
        userService = new UserService();
        userService.loginUser(USER_ADMIN_NAME, USER_PASSWORD);
    }

    @Test
    void testAddTraining() {
        // assert throwing an exception ObjectNotFoundException
        assertThrows(ObjectNotFoundException.class, () -> trainingService.getAllTrainings());

        long creationTimestamp = 1716824237136L;
        long expectedTimestamp = 1716750000000L;
        Date currentDate = new Date(creationTimestamp);

        Training training = new Training();
        training.setUserId(USER_ADMIN_ID);
        training.setTrainingTypeId(1L);
        training.setDate(currentDate);
        training.setDuration(60);
        training.setCaloriesBurned(1000);
        training.setAdditionalInfo("Test training");
        trainingService.addTraining(training);

        List<TrainingDTO> trainingsList;
        // assert that object exists in the database
        trainingsList = trainingService.getAllTrainings();
        assertNotNull(trainingsList);
        assertEquals(1, trainingsList.size());

        // assert that Training object was created in the database
        // and has the same values as the original object
        TrainingDTO retrievedTraining = trainingsList.get(0);
        assertNotNull(retrievedTraining);
        assertSame(retrievedTraining.trainingTypeId(), training.getTrainingTypeId());
        assertEquals(retrievedTraining.date().getTime(), expectedTimestamp);
        assertEquals(retrievedTraining.duration(), training.getDuration());
        assertEquals(retrievedTraining.caloriesBurned(), training.getCaloriesBurned());
        assertEquals(retrievedTraining.additionalInfo(), training.getAdditionalInfo());

        // delete created object
        trainingService.deleteTraining(retrievedTraining.id());
        assertThrows(ObjectNotFoundException.class, () -> trainingService.getAllTrainings());
    }

    @Test
    void testGetAllTrainings() {
        Training training = new Training();
        training.setUserId(USER_ADMIN_ID);
        training.setTrainingTypeId(1L);
        training.setDate(new Date());
        training.setDuration(60);
        training.setCaloriesBurned(1000);
        training.setAdditionalInfo("Test training");
        trainingService.addTraining(training);

        List<TrainingDTO> trainingsList = trainingService.getAllTrainings();
        assertNotNull(trainingsList);
        assertEquals(1, trainingsList.size());
        TrainingDTO retrievedTraining = trainingsList.get(0);
        assertNotNull(retrievedTraining);
        assertEquals(training.getTrainingTypeId(), retrievedTraining.trainingTypeId());
        assertEquals(training.getDuration(), retrievedTraining.duration());
        assertEquals(training.getCaloriesBurned(), retrievedTraining.caloriesBurned());
        assertEquals(training.getAdditionalInfo(), retrievedTraining.additionalInfo());

        // delete created object
        trainingService.deleteTraining(retrievedTraining.id());
        assertThrows(ObjectNotFoundException.class, () -> trainingService.getAllTrainings());
    }
}
