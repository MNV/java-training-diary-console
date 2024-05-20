package ru.ylab;

import java.util.Map;

public record Statistics(
    Map<String, Integer> monthlyCalories,
    Map<String, Integer> monthlyDurations,
    int totalCalories,
    int totalDuration
) {
    @Override
    public String toString() {
        if (totalCalories == 0 && totalDuration == 0) {
            return "No statistics available.";
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Monthly Statistics:\n");
        for (String month : monthlyCalories.keySet()) {
            stringBuilder.append(month).append(": ")
                .append("calories burned = ").append(monthlyCalories.get(month))
                .append(", duration = ").append(monthlyDurations.get(month)).append(" minutes\n");
        }
        stringBuilder.append("\nTotal Statistics: ")
            .append("calories burned = ").append(totalCalories)
            .append(", duration = ").append(totalDuration).append(" minutes\n");

        return stringBuilder.toString();
    }
}
