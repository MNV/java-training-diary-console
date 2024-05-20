package ru.ylab;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Training {
    private static int count = 0;
    private int id = 0;
    private String trainingType;
    private Date date;
    private int duration;
    private int caloriesBurned;
    private String additionalInfo;
    private String username;

    public Training(
        String trainingType,
        Date date,
        int duration,
        int caloriesBurned
    ) {

        this.trainingType = trainingType;
        this.date = date;
        this.duration = duration;
        this.caloriesBurned = caloriesBurned;
        Training.count += 1;
        this.id = Training.count;
    }

    public int getId() {
        return id;
    }

    public String getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        return "Training{" +
                "id=" + id +
                ", trainingType='" + trainingType + '\'' +
                ", date=" + dateFormat.format(date) +
                ", duration=" + duration +
                ", caloriesBurned=" + caloriesBurned +
                ", additionalInfo='" + additionalInfo + '\'' +
                '}';
    }
}
