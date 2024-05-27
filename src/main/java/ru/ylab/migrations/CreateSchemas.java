package ru.ylab.migrations;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


/**
 * Creating schemas in the database.
 * It is necessary to run this before applying migrations.
 */
public class CreateSchemas {
    private static final String PROPERTIES_FILE_NAME = "liquibase.properties";

    public static void main(String[] args) {
        Properties properties = new Properties();

        // loading database configuration properties
        try (InputStream input = CreateSchemas.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            if (input == null) {
                System.out.printf("Unable to find %s file.%n", PROPERTIES_FILE_NAME);
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        String defaultSchemaName = properties.getProperty("defaultSchemaName");
        String liquibaseSchemaName = properties.getProperty("liquibaseSchemaName");

        try (Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);

            try {
                // creating the application schema
                statement.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + defaultSchemaName);
                // creating the Liquibase schema
                statement.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + liquibaseSchemaName);

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
