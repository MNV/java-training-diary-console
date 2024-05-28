package ru.ylab.dto;

public record StatisticsDTO(
    String monthYear, int trainingsCount, int duration, int caloriesBurned
) {}
