package ru.ylab.dto;

import java.util.Date;

public record TrainingDTO(
    Long id, Long userId, String username, Long trainingTypeId, String trainingType, Date date,
    int duration, int caloriesBurned, String additionalInfo
) {}
