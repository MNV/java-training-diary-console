package ru.ylab.repositories;

import ru.ylab.dto.TrainingTypeDTO;
import ru.ylab.integrations.DataSource;
import ru.ylab.models.TrainingType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrainingTypeRepository implements BaseRepository<TrainingType, TrainingTypeDTO>{

    private final DataSource dataSource;

    public TrainingTypeRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Long create(TrainingType trainingType) {
        String sql = "INSERT INTO training_types (name) VALUES (?) RETURNING id;";
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, trainingType.getName());
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
    public Optional<TrainingTypeDTO> read(Long id) {
        String sql = "SELECT id, name FROM training_types WHERE id = ?;";
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

    public Optional<TrainingTypeDTO> readByName(String name) {
        String sql = "SELECT id, name FROM training_types WHERE LOWER(name) = LOWER(?);";
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, name);
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
    public void update(TrainingType trainingType) {
        String sql = "UPDATE training_types SET name = ? WHERE id = ?;";
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, trainingType.getName());
            statement.setLong(2, trainingType.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int delete(Long id) {
        String sql = "DELETE FROM training_types WHERE id = ?";
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            return statement.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return 0;
    }

    public List<TrainingTypeDTO> findAll() {
        List<TrainingTypeDTO> trainings = new ArrayList<>();
        String sql = "SELECT id, name FROM training_types;";

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

    public boolean checkIfTrainingTypeExists(String trainingTypeName) {
        String sql = "SELECT 1 FROM training_types WHERE LOWER(name) = LOWER(?);";

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, trainingTypeName);

            try (ResultSet resultSet = statement.executeQuery()) {
                boolean result = resultSet.next();
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private TrainingTypeDTO resultSetToDTO(ResultSet resultSet) throws SQLException {
        return new TrainingTypeDTO(
            resultSet.getLong("id"),
            resultSet.getString("name")
        );
    }
}
