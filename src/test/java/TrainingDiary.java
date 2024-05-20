import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ylab.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainingDiaryTest {

    private TrainingDiary diary;
    private SimpleDateFormat dateFormat;
    private String userUsername;
    private String userPassword;

    @BeforeEach
    void setUp() {
        diary = new TrainingDiary();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        userUsername = "user_username";
        userPassword = "user_password";
    }

    @Test
    void testRegisterUser() throws UserExistsException {
        assertFalse(diary.loginUser(userUsername, userPassword));
        diary.registerUser(userUsername, userPassword, false);
        assertTrue(diary.loginUser(userUsername, userPassword));
        assertTrue(diary.isAuthenticated());
    }

    @Test
    void testRegisterUserExists() {
        assertThrows(UserExistsException.class, () -> {
            diary.registerUser("username_same", "user_password", false);
            diary.registerUser("username_same", "user_password_not_same", false);
        });
    }

    @Test
    void testLoginUser() throws UserExistsException {
        diary.registerUser(userUsername, userPassword, false);
        assertTrue(diary.loginUser(userUsername, userPassword));
        assertTrue(diary.isAuthenticated());
    }

    @Test
    void testLoginUserInvalidPassword() throws UserExistsException {
        diary.registerUser(userUsername, userPassword, false);
        assertFalse(diary.loginUser(userUsername, "wrong_password"));
    }

    @Test
    void testLogout() throws UserExistsException {
        diary.registerUser(userUsername, userPassword, false);
        diary.loginUser(userUsername, userPassword);
        diary.logout();
        assertFalse(diary.isAuthenticated());
    }

    @Test
    void testCanAddTraining() throws UserExistsException, ParseException {
        diary.registerUser(userUsername, userPassword, false);
        diary.loginUser(userUsername, userPassword);
        String sameDate = "15.05.2024";

        Date date = dateFormat.parse(sameDate);

        assertTrue(diary.canAddTraining("Swimming", date));

        Training training = new Training("Swimming", dateFormat.parse(sameDate), 45, 250);
        diary.addTraining(training);

        assertFalse(diary.canAddTraining("Swimming", date));
    }

    @Test
    void testAddTraining() throws UserExistsException, ParseException {
        diary.registerUser(userUsername, userPassword, false);
        diary.loginUser(userUsername, userPassword);

        assertEquals(0, diary.getTrainings().size());

        Date date = dateFormat.parse("15.05.2024");
        Training training = new Training("Swimming", date, 45, 250);
        training.setAdditionalInfo("Some additional information here.");

        diary.addTraining(training);
        List<Training> trainings = diary.getTrainings();
        assertEquals(1, trainings.size());

        Training trainingObject = trainings.get(0);
        assertEquals(3, trainingObject.getId());
        assertEquals("Swimming", trainingObject.getTrainingType());
        assertEquals(date, trainingObject.getDate());
        assertEquals(45, trainingObject.getDuration());
        assertEquals(250, trainingObject.getCaloriesBurned());
        assertEquals("Some additional information here.", trainingObject.getAdditionalInfo());
        assertEquals(userUsername, trainingObject.getUsername());
    }

    @Test
    void testGetTrainings() throws UserExistsException, ParseException {
        diary.registerUser(userUsername, userPassword, false);
        diary.loginUser(userUsername, userPassword);

        assertEquals(0, diary.getTrainings().size());

        Training training1 = new Training("Swimming", dateFormat.parse("15.05.2024"),45, 200);
        Training training2 = new Training("Running", dateFormat.parse("25.05.2024"),120, 450);

        diary.addTraining(training1);
        diary.addTraining(training2);

        List<Training> trainings = diary.getTrainings();
        assertEquals(2, trainings.size());
    }

    @Test
    void testEditTraining() throws UserExistsException, ParseException {
        diary.registerUser(userUsername, userPassword, false);
        diary.loginUser(userUsername, userPassword);

        Training training = new Training("Swimming", dateFormat.parse("15.05.2024"),45, 200);
        training.setAdditionalInfo("Some additional information here.");
        diary.addTraining(training);

        assertEquals("Swimming", training.getTrainingType());
        diary.editTrainingType(training, "Running");
        assertEquals("Running", training.getTrainingType());

        assertEquals(dateFormat.parse("15.05.2024"), training.getDate());
        diary.editTrainingDate(training, dateFormat.parse("20.06.2024"));
        assertEquals(dateFormat.parse("20.06.2024"), training.getDate());

        assertEquals(45, training.getDuration());
        diary.editTrainingDuration(training, 85);
        assertEquals(85, training.getDuration());

        assertEquals(200, training.getCaloriesBurned());
        diary.editTrainingCaloriesBurned(training, 550);
        assertEquals(550, training.getCaloriesBurned());

        assertEquals("Some additional information here.", training.getAdditionalInfo());
        diary.editTrainingAdditionalInfo(training, "New information.");
        assertEquals("New information.", training.getAdditionalInfo());
    }

    @Test
    void testDeleteTraining() throws UserExistsException, ParseException {
        diary.registerUser(userUsername, userPassword, false);
        diary.loginUser(userUsername, userPassword);

        assertEquals(0, diary.getTrainings().size());

        Training training = new Training("Swimming", dateFormat.parse("15.05.2024"),45, 200);
        diary.addTraining(training);

        assertEquals(1, diary.getTrainings().size());
        assertTrue(diary.deleteTraining(training.getId()));

        assertEquals(0, diary.getTrainings().size());
        assertFalse(diary.deleteTraining(training.getId()));
    }

    @Test
    void testGetStatistics() throws UserExistsException, ParseException {
        diary.registerUser(userUsername, userPassword, false);
        diary.loginUser(userUsername, userPassword);

        Training training1 = new Training("Swimming", dateFormat.parse("15.05.2024"),45, 200);
        Training training2 = new Training("Running", dateFormat.parse("25.05.2024"),120, 450);
        Training training3 = new Training("Running", dateFormat.parse("05.06.2024"),60, 150);

        diary.addTraining(training1);
        diary.addTraining(training2);
        diary.addTraining(training3);

        Statistics stats = diary.getStatistics();

        assertEquals(800, stats.totalCalories());
        assertEquals(225, stats.totalDuration());
        assertEquals(2, stats.monthlyCalories().size());
        assertEquals(2, stats.monthlyDurations().size());
    }

    @Test
    void testGetAuditLog() throws UserExistsException, AccessDeniedException {
        diary.registerUser(userUsername, userPassword, true);
        diary.loginUser(userUsername, userPassword);

        List<String> auditLog = diary.getAuditLog();
        assertNotNull(auditLog);
        assertFalse(auditLog.isEmpty());
    }

    @Test
    void testGetAuditLog_AccessDenied() throws UserExistsException {
        diary.registerUser(userUsername, userPassword, false);
        diary.loginUser(userUsername, userPassword);

        assertThrows(AccessDeniedException.class, () -> diary.getAuditLog());
    }
}
