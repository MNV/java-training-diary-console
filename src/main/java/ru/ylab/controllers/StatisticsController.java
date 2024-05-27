package ru.ylab.controllers;

import ru.ylab.dto.StatisticsDTO;
import ru.ylab.services.StatisticsService;
import ru.ylab.utils.Console;

import java.util.List;


public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController() {
        this.statisticsService = new StatisticsService();
    }

    public void viewStatistics() {
        List<StatisticsDTO> statistics = statisticsService.getStatistics();
        if (statistics.isEmpty()) {
            System.out.println(Console.warning("No statistics available."));
            return;
        }

        int totalTrainings = 0;
        int totalDuration = 0;
        int totalCaloriesBurned = 0;

        String[][] tableData = new String[statistics.size() + 2][4];
        for (int i = 0; i < statistics.size(); i++) {
            StatisticsDTO monthlyStats = statistics.get(i);
            tableData[i][0] = monthlyStats.monthYear();
            tableData[i][1] = String.valueOf(monthlyStats.trainingsCount());
            tableData[i][2] = String.valueOf(monthlyStats.duration());
            tableData[i][3] = String.valueOf(monthlyStats.caloriesBurned());
            totalTrainings += monthlyStats.trainingsCount();
            totalDuration += monthlyStats.duration();
            totalCaloriesBurned += monthlyStats.caloriesBurned();
        }
        for (int i = 0; i < 4; i++) {
            tableData[statistics.size()][i] = "";
        }
        tableData[statistics.size() + 1][0] = "Total";
        tableData[statistics.size() + 1][1] = String.valueOf(totalTrainings);
        tableData[statistics.size() + 1][2] = String.valueOf(totalDuration);
        tableData[statistics.size() + 1][3] = String.valueOf(totalCaloriesBurned);

        System.out.println("Monthly Training Statistics:");
        System.out.println(Console.createTable(
            new String[]{"Month", "Trainings", "Duration (min)", "Calories Burned"}, tableData)
        );
    }
}
