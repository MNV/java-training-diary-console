package ru.ylab;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

record User(String username, String password, boolean isAdmin) {}

/**
 * Functions for managing training diary.
 */
public class TrainingDiary {

    private final Map<String, User> users = new HashMap<>();
    private final Map<User, List<Training>> trainings = new HashMap<>();
    private final List<String> auditLog = new ArrayList<>();
    private User loggedInUser;

    /**
     * Register a new user.
     *
     * @param username
     * @param password
     * @param isAdmin
     * @throws UserExistsException
     */
    public void registerUser(String username, String password, boolean isAdmin) throws UserExistsException {
        if (users.containsKey(username)) {
            throw new UserExistsException("Username already exists.");
        }
        users.put(username, new User(username, password, isAdmin));
        auditLog.add("User registered: " + username);
    }

    /**
     * Log in a user.
     *
     * @param username
     * @param password
     */
    public boolean loginUser(String username, String password) {
        User user = users.get(username);
        if (user != null && user.password().equals(password)) {
            loggedInUser = user;
            auditLog.add("User logged in: " + username);
            return true;
        }
        return false;
    }

    /**
     * Log out the current user.
     */
    public void logout() {
        if (loggedInUser != null) {
            auditLog.add("User logged out: " + loggedInUser.username());
            loggedInUser = null;
        }
    }

    /**
     * Check if a user is authenticated.
     *
     * @return Boolean result.
     */
    public boolean isAuthenticated() {
        return loggedInUser != null;
    }

    /**
     * Check if a training can be added on a specific date.
     *
     * @param trainingType Type of the training.
     * @param date Date to add the training.
     */
    public boolean canAddTraining(String trainingType, Date date) {
        List<Training> userTrainings = trainings.get(loggedInUser);
        if (userTrainings != null) {
            return userTrainings.stream().noneMatch(t -> t.getTrainingType().equals(trainingType) && isSameDay(t.getDate(), date));
        }
        return true;
    }

    /**
     * Add a new training.
     *
     * @param training Training object to add.
     */
    public void addTraining(Training training) {
        trainings.computeIfAbsent(loggedInUser, k -> new ArrayList<>()).add(training);
        training.setUsername(loggedInUser.username());
        auditLog.add("Training added by " + loggedInUser.username() + ": " + training);
    }

    /**
     * Get the list of trainings for the logged-in user.
     *
     * @return List of trainings.
     */
    public List<Training> getTrainings() {
        List<Training> userTrainings;
        if (loggedInUser.isAdmin()) {
            userTrainings = trainings.values().stream()
                                     .flatMap(Collection::stream)
                                     .sorted(Comparator.comparing(Training::getDate).reversed())
                                     .collect(Collectors.toList());
        } else {
            userTrainings = trainings.getOrDefault(loggedInUser, new ArrayList<>())
                                     .stream()
                                     .sorted(Comparator.comparing(Training::getDate).reversed())
                                     .collect(Collectors.toList());
        }
        auditLog.add("Trainings viewed by: " + loggedInUser.username());
        return userTrainings;
    }

    /**
     * Get a training by its ID.
     *
     * @param trainingId Training ID.
     */
    public Training getTrainingById(int trainingId) {
        List<Training> userTrainings = trainings.get(loggedInUser);
        if (userTrainings != null) {
            return userTrainings.stream()
                                .filter(t -> t.getId() == trainingId)
                                .findFirst()
                                .orElse(null);
        }
        return null;
    }

    public void editTrainingType(Training training, String trainingType) {
        if (training != null) {
            training.setTrainingType(trainingType);
            auditLog.add(String.format("Training with ID %d updated by %s: training type.", training.getId(), loggedInUser.username()));
        }
    }

    public void editTrainingDate(Training training, Date date) {
        if (training != null) {
            training.setDate(date);
            auditLog.add(String.format("Training with ID %d updated by %s: date.", training.getId(), loggedInUser.username()));
        }
    }

    public void editTrainingDuration(Training training, int duration) {
        if (training != null) {
            training.setDuration(duration);
            auditLog.add(String.format("Training with ID %d updated by %s: duration.", training.getId(), loggedInUser.username()));
        }
    }

    public void editTrainingCaloriesBurned(Training training, int calories) {
        if (training != null) {
            training.setCaloriesBurned(calories);
            auditLog.add(String.format("Training with ID %d updated by %s: calories burned.", training.getId(), loggedInUser.username()));
        }
    }

    public void editTrainingAdditionalInfo(Training training, String info) {
        if (training != null) {
            training.setAdditionalInfo(info);
            auditLog.add(String.format("Training with ID %d updated by %s: additional info.", training.getId(), loggedInUser.username()));
        }
    }

    /**
     * Delete a training by its ID.
     *
     * @param trainingId Training ID.
     */
    public boolean deleteTraining(int trainingId) {
        boolean result = false;
        if (loggedInUser.isAdmin()) {
            for (List<Training> trainingList : trainings.values()) {
                result = trainingList.removeIf(training -> training.getId() == trainingId);
                if (result) {
                    break;
                }
            }
        } else {
            List<Training> userTrainings = trainings.get(loggedInUser);
            if (userTrainings != null) {
                result = userTrainings.removeIf(training -> training.getId() == trainingId);
            }
        }
        if (result) {
            auditLog.add(String.format("Training with ID %d deleted by %s.", trainingId, loggedInUser.username()));
        } else {
            auditLog.add(String.format("Attempt to delete training with ID %d by %s. Training not found.", trainingId, loggedInUser.username()));
        }
        return result;
    }

    /**
     * Get statistics for the logged-in user.
     *
     * @return Statistics object.
     */
    public Statistics getStatistics() {
        Map<String, Integer> monthlyCalories = new HashMap<>();
        Map<String, Integer> monthlyDurations = new HashMap<>();
        int totalCalories = 0;
        int totalDuration = 0;
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy");
        List<Training> userTrainings = trainings.getOrDefault(loggedInUser, new ArrayList<>());

        for (Training userTraining : userTrainings) {
            String month = monthFormat.format(userTraining.getDate());
            monthlyCalories.put(month, monthlyCalories.getOrDefault(month, 0) + userTraining.getCaloriesBurned());
            monthlyDurations.put(month, monthlyDurations.getOrDefault(month, 0) + userTraining.getDuration());
            totalCalories += userTraining.getCaloriesBurned();
            totalDuration += userTraining.getDuration();
        }

        Statistics statistics = new Statistics(monthlyCalories, monthlyDurations, totalCalories, totalDuration);
        auditLog.add("Statistics viewed by: " + loggedInUser.username());
        return statistics;
    }

    /**
     * Get the audit log (admin only).
     *
     * @throws AccessDeniedException
     */
    public List<String> getAuditLog() throws AccessDeniedException {
        if (!loggedInUser.isAdmin()) {
            throw new AccessDeniedException("Access denied.");
        }
        return Collections.unmodifiableList(auditLog);
    }

    /**
     * Check if two dates fall on the same day.
     *
     * @param date1 First date.
     * @param date2 Second date.
     */
    private boolean isSameDay(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(date1);
        calendar2.setTime(date2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }
}
