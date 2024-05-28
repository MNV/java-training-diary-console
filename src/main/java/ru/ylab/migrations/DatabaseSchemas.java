package ru.ylab.migrations;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import lombok.Getter;
import ru.ylab.integrations.DataSource;
import ru.ylab.settings.PropertiesReader;
import ru.ylab.settings.PropertyDTO;


/**
 * Creating schemas in the database.
 * It is necessary to run this before applying migrations.
 */
public class DatabaseSchemas {
    private final DataSource dataSource;
    @Getter
    private String defaultSchemaName;
    @Getter
    private String liquibaseSchemaName;

    public DatabaseSchemas(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createSchemas() {
        PropertiesReader propertiesReader = new PropertiesReader();
        PropertyDTO propertyDTO = propertiesReader.read();
        defaultSchemaName = propertyDTO.defaultSchemaName();
        liquibaseSchemaName = propertyDTO.liquibaseSchemaName();
        try (
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()
        ) {
            connection.setAutoCommit(false);

            try {
                // creating the application schema
                statement.executeUpdate(String.format("CREATE SCHEMA IF NOT EXISTS %s;", defaultSchemaName));
                // creating the Liquibase schema
                statement.executeUpdate(String.format("CREATE SCHEMA IF NOT EXISTS %s;", liquibaseSchemaName));

                connection.commit();
                System.out.println("Database schemas created successfully. Now you can apply migrations.");

            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                System.err.println("Failed to create database schemas. Transaction rolled back.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
