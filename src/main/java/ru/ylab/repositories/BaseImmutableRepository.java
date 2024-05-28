package ru.ylab.repositories;

import ru.ylab.exceptions.ConstraintViolationException;
import ru.ylab.models.BaseModel;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public interface BaseImmutableRepository<M extends BaseModel, D> {
    String CODE_FOREIGN_KEY_VIOLATION = "23503";

    Long create(M model);
    Optional<D> read(Long id);
    int delete(Long id);

    default void handleSQLException(SQLException e) {
        if (Objects.equals(e.getSQLState(), CODE_FOREIGN_KEY_VIOLATION)) {
            throw new ConstraintViolationException("Foreign key constraint violation.");
        }
        throw new RuntimeException(e);
    }
}
