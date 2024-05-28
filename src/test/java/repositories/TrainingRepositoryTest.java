package repositories;

import extensions.TestDatabaseExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.ylab.dto.TrainingDTO;
import ru.ylab.models.Training;
import ru.ylab.repositories.TrainingRepository;

import java.util.Date;

import static fixtures.DatabaseFixtures.USER_ADMIN_ID;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TestDatabaseExtension.class)
class TrainingRepositoryTest {

    TrainingRepository trainingRepository;

    @BeforeEach
    public void setUp() {
        trainingRepository = new TrainingRepository(TestDatabaseExtension.DATA_SOURCE);
    }

    @Test
    void testCreate() {
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
        Long trainingId = trainingRepository.create(training);
        assertNotNull(trainingId);

        // assert that Training object was created in the database
        // and has the same values as the original object
        assertTrue(trainingRepository.read(trainingId).isPresent());
        TrainingDTO retrievedTraining = trainingRepository.read(trainingId).get();
        assertNotNull(retrievedTraining);
        assertSame(retrievedTraining.trainingTypeId(), training.getTrainingTypeId());
        assertEquals(retrievedTraining.date().getTime(), expectedTimestamp);
        assertEquals(training.getDate().getTime(), creationTimestamp);
        assertEquals(retrievedTraining.duration(), training.getDuration());
        assertEquals(retrievedTraining.caloriesBurned(), training.getCaloriesBurned());
        assertEquals(retrievedTraining.additionalInfo(), training.getAdditionalInfo());

        // delete created object
        trainingRepository.delete(trainingId);
        assertFalse(trainingRepository.read(trainingId).isPresent());
    }

    @Test
    void testUpdate() {
        Long creationTimestamp = 1716824237136L;
        Long expectedTimestamp = 1716750000000L;
        Date currentDate = new Date(creationTimestamp);

        Training training = new Training();
        training.setUserId(USER_ADMIN_ID);
        training.setTrainingTypeId(1L);
        training.setDate(currentDate);
        training.setDuration(60);
        training.setCaloriesBurned(1000);
        training.setAdditionalInfo("Test training");

        Long trainingId = trainingRepository.create(training);
        assertNotNull(trainingId);
        assertTrue(trainingRepository.read(trainingId).isPresent());

        // assert that Training object was created in the database
        // and has the same values as the original object
        TrainingDTO retrievedTraining = trainingRepository.read(trainingId).get();
        assertNotNull(retrievedTraining);
        assertSame(retrievedTraining.trainingTypeId(), training.getTrainingTypeId());
        assertEquals(retrievedTraining.date().getTime(), expectedTimestamp);
        assertEquals(training.getDate().getTime(), creationTimestamp);
        assertEquals(retrievedTraining.duration(), training.getDuration());
        assertEquals(retrievedTraining.caloriesBurned(), training.getCaloriesBurned());
        assertEquals(retrievedTraining.additionalInfo(), training.getAdditionalInfo());

        // update the created object
        Long updatedTimestamp = 1716873980321L;
        Long expectedUpdatedTimestamp = 1716836400000L;
        Date updatedDate = new Date(updatedTimestamp);

        training.setId(trainingId);
        training.setDate(updatedDate);
        training.setDuration(90);
        training.setCaloriesBurned(2000);
        training.setAdditionalInfo("Updated training");
        trainingRepository.update(training);

        TrainingDTO updatedTraining = trainingRepository.read(trainingId).get();
        assertNotNull(updatedTraining);
        assertEquals(updatedTraining.date().getTime(), expectedUpdatedTimestamp);
        assertEquals(updatedTraining.duration(), training.getDuration());
        assertEquals(updatedTraining.caloriesBurned(), training.getCaloriesBurned());
        assertEquals(updatedTraining.additionalInfo(), training.getAdditionalInfo());

        // delete created object
        trainingRepository.delete(trainingId);
        assertFalse(trainingRepository.read(trainingId).isPresent());
    }

    @Test
    void testDelete() {
        Training training = new Training();
        training.setUserId(USER_ADMIN_ID);
        training.setTrainingTypeId(1L);
        training.setDate(new Date());
        training.setDuration(60);
        training.setCaloriesBurned(1000);
        training.setAdditionalInfo("Test training");
        Long trainingId = trainingRepository.create(training);

        assertNotNull(trainingId);
        assertTrue(trainingRepository.read(trainingId).isPresent());
        trainingRepository.delete(trainingId);
        assertFalse(trainingRepository.read(trainingId).isPresent());
    }
}
