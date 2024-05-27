package ru.ylab.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Training implements BaseModel {
    private Long id;
    private Long userId;
    private Long trainingTypeId;
    private Date date;
    private int duration;
    private int caloriesBurned;
    private String additionalInfo;
}
