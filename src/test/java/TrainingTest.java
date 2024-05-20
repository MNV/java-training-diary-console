import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.ylab.Training;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class TrainingTest {
    private static Training training;

    @BeforeAll
    public static void setUp() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        training = new Training("Swimming", dateFormat.parse("15.05.2024"), 45, 250);
        training.setAdditionalInfo("Some additional information here.");
        training.setUsername("some_username");
    }

    @Test
    public void testGetUsername() {
        assertEquals("some_username", training.getUsername());
    }

    @Test
    public void testGetTrainingType() {
        assertEquals("Swimming", training.getTrainingType());
    }

    @Test
    public void testGetDate() {
        assertNotNull(training.getDate());
    }

    @Test
    public void testGetDuration() {
        assertEquals(45, training.getDuration());
    }

    @Test
    public void testGetCaloriesBurned() {
        assertEquals(250, training.getCaloriesBurned());
    }

    @Test
    public void testGetAdditionalInfo() {
        assertEquals("Some additional information here.", training.getAdditionalInfo());
    }

    @Test
    public void testToString() {
        String expectedString = "Training{" +
            "id=1, trainingType='Swimming', date=15.05.2024, duration=45, " +
            "caloriesBurned=250, additionalInfo='Some additional information here.'" +
        "}";

        assertEquals(expectedString, training.toString());
    }
}
