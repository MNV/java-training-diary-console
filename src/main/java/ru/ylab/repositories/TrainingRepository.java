package ru.ylab.repositories;

import ru.ylab.dto.StatisticsDTO;
import ru.ylab.dto.TrainingDTO;
import ru.ylab.integrations.DataSource;
import ru.ylab.models.Training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class TrainingRepository implements BaseRepository<Training, TrainingDTO>{

    private final DataSource dataSource;

    public TrainingRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Long create(Training training) {
        String sql = """
            INSERT INTO trainings (user_id, training_type_id, date, duration, calories_burned, additional_info)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id;""";
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, training.getUserId());
            statement.setLong(2, training.getTrainingTypeId());
            statement.setDate(3, new java.sql.Date(training.getDate().getTime()));
            statement.setInt(4, training.getDuration());
            statement.setInt(5, training.getCaloriesBurned());
            statement.setString(6, training.getAdditionalInfo());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Could not retrieve sequence ID.");
    }

    @Override
    public Optional<TrainingDTO> read(Long id) {
        String sql = """
            SELECT
                trainings.id, user_id, users.username, training_type_id, training_types.name training_type,
                date, duration, calories_burned, additional_info
            FROM trainings
                INNER JOIN training_types
                    ON trainings.training_type_id = training_types.id
                INNER JOIN users
                    ON trainings.user_id = users.id
            WHERE trainings.id = ?;""";

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(resultSetToDTO(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public void update(Training training) {
        String sql = """
            UPDATE trainings
            SET training_type_id = ?, date = ?, duration = ?, calories_burned = ?, additional_info = ?
            WHERE id = ?;""";

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, training.getTrainingTypeId());
            statement.setDate(2, new java.sql.Date(training.getDate().getTime()));
            statement.setInt(3, training.getDuration());
            statement.setInt(4, training.getCaloriesBurned());
            statement.setString(5, training.getAdditionalInfo());
            statement.setLong(6, training.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int delete(Long id) {
        String sql = "DELETE FROM trainings WHERE id = ?";
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TrainingDTO> findAll() {
        List<TrainingDTO> trainings = new ArrayList<>();
        String sql = """
            SELECT
                trainings.id, user_id, users.username, training_type_id, training_types.name training_type,
                date, duration, calories_burned, additional_info
            FROM trainings
                INNER JOIN training_types
                    ON trainings.training_type_id = training_types.id
                INNER JOIN users
                    ON trainings.user_id = users.id
            ORDER BY date DESC;""";

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    trainings.add(resultSetToDTO(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return trainings;
    }

    public List<TrainingDTO> findAllByUserId(Long userId) {
        List<TrainingDTO> trainings = new ArrayList<>();
        String sql = """
            SELECT
                trainings.id, user_id, users.username, training_type_id, training_types.name training_type,
                date, duration, calories_burned, additional_info
            FROM trainings
                INNER JOIN training_types
                    ON trainings.training_type_id = training_types.id
                INNER JOIN users
                    ON trainings.user_id = users.id
            WHERE trainings.user_id = ?
            ORDER BY date DESC;""";

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    trainings.add(resultSetToDTO(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return trainings;
    }

    public boolean checkIfTrainingExists(Long userId, Date date, Long trainingTypeId) {
        String sql = "SELECT 1 FROM trainings WHERE user_id = ? AND DATE(date) = DATE(?) AND training_type_id = ?;";

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, userId);
            statement.setDate(2, new java.sql.Date(date.getTime()));
            statement.setLong(3, trainingTypeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<StatisticsDTO> getMonthlyStatistics(Long userId) {
        String sql = """
             SELECT
                TO_CHAR(date, 'MM.yyyy') AS month_year, COUNT(*) AS trainings_count,
                SUM(duration) AS duration, SUM(calories_burned) AS calories_burned
             FROM trainings
             WHERE trainings.user_id = ?
             GROUP BY month_year
             ORDER BY month_year;""";

        List<StatisticsDTO> statistics = new ArrayList<>();
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String monthYear = resultSet.getString("month_year");
                    int trainingsCount = resultSet.getInt("trainings_count");
                    int duration = resultSet.getInt("duration");
                    int caloriesBurned = resultSet.getInt("calories_burned");
                    statistics.add(new StatisticsDTO(monthYear, trainingsCount, duration, caloriesBurned));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return statistics;
    }

    private TrainingDTO resultSetToDTO(ResultSet resultSet) throws SQLException {
        return new TrainingDTO(
            resultSet.getLong("id"),
            resultSet.getLong("user_id"),
            resultSet.getString("username"),
            resultSet.getLong("training_type_id"),
            resultSet.getString("training_type"),
            resultSet.getDate("date"),
            resultSet.getInt("duration"),
            resultSet.getInt("calories_burned"),
            resultSet.getString("additional_info")
        );
    }
}
