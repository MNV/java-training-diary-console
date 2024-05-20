import org.junit.jupiter.api.Test;
import ru.ylab.Statistics;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatisticsTest {

    @Test
    public void testToStringEmpty() {
        Map<String, Integer> empty = new HashMap<>();
        Statistics statistics = new Statistics(empty, empty, 0, 0);
        String expected = "No statistics available.";
        assertEquals(expected, statistics.toString());
    }

    @Test
    public void testToStringNonEmpty() {
        Map<String, Integer> monthlyCalories = new HashMap<>();
        monthlyCalories.put("January", 500);
        monthlyCalories.put("February", 650);

        Map<String, Integer> monthlyDurations = new HashMap<>();
        monthlyDurations.put("January", 450);
        monthlyDurations.put("February", 760);

        int totalCalories = 1150;
        int totalDuration = 1160;

        Statistics statistics = new Statistics(monthlyCalories, monthlyDurations, totalCalories, totalDuration);
        String expected =
                """
                Monthly Statistics:
                January: calories burned = 500, duration = 450 minutes
                February: calories burned = 650, duration = 760 minutes

                Total Statistics: calories burned = 1150, duration = 1160 minutes
                """;

        assertEquals(expected, statistics.toString());
    }
}
