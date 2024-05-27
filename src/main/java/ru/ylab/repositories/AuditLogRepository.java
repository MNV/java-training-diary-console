package ru.ylab.repositories;

import ru.ylab.dto.AuditLogDTO;
import ru.ylab.integrations.DataSource;
import ru.ylab.models.AuditLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuditLogRepository implements BaseImmutableRepository<AuditLog, AuditLogDTO>{

    private final DataSource dataSource;

    public AuditLogRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Long create(AuditLog auditLog) {
        String sql = "INSERT INTO audit_log (user_id, action) VALUES (?, ?) RETURNING id;";
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, auditLog.getUserId());
            statement.setString(2, auditLog.getAction());

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
    public Optional<AuditLogDTO> read(Long id) {
        String sql = "SELECT id, action, user_id, created_at FROM audit_log WHERE id = ?;";
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
    public int delete(Long id) {
        String sql = "DELETE FROM audit_log WHERE id = ?";
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

    public List<AuditLogDTO> findAll() {
        List<AuditLogDTO> auditLog = new ArrayList<>();
        String sql = """
                SELECT user_id, username, action, created_at
                FROM audit_log
                    INNER JOIN users ON audit_log.user_id = users.id
                ORDER BY created_at DESC;""";

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    auditLog.add(resultSetToDTO(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return auditLog;
    }

    private AuditLogDTO resultSetToDTO(ResultSet resultSet) throws SQLException {
        return new AuditLogDTO(
            resultSet.getLong("user_id"),
            resultSet.getString("username"),
            resultSet.getString("action"),
            resultSet.getTimestamp("created_at")
        );
    }
}
