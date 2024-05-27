package ru.ylab.integrations;

import ru.ylab.exceptions.DatabaseConfigurationException;
import ru.ylab.migrations.CreateSchemas;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataSource {
    private static final String PROPERTIES_FILE_NAME = "liquibase.properties";

    public Connection getConnection() throws SQLException {
        Properties properties = new Properties();

        // loading database configuration properties
        try (InputStream input = CreateSchemas.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            if (input == null) {
                throw new DatabaseConfigurationException(String.format("Unable to find '%s' file.%n", PROPERTIES_FILE_NAME));
            }
            properties.load(input);
        } catch (IOException e) {
            throw new DatabaseConfigurationException(e.getMessage());
        }

        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        return DriverManager.getConnection(url, username, password);
    }
}
