package ru.ylab.services;

import ru.ylab.app.Application;
import ru.ylab.dto.StatisticsDTO;
import ru.ylab.dto.UserDTO;
import ru.ylab.repositories.TrainingRepository;

import java.util.List;

public class StatisticsService {

    private final TrainingRepository trainingRepository;

    public StatisticsService() {
        this.trainingRepository = new TrainingRepository(Application.dataSource);
    }

    /**
     * Get statistics for the logged-in user.
     *
     * @return Statistics object.
     */
    public List<StatisticsDTO> getStatistics() {
        UserDTO currentUser = Application.getUserSession().getLoggedInUser();
        Application.getLogger().log("Viewed training statistics.");

        return trainingRepository.getMonthlyStatistics(currentUser.id());
    }
}
