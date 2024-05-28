package ru.ylab.integrations;

import ru.ylab.exceptions.DatabaseConfigurationException;
import ru.ylab.migrations.DatabaseSchemas;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataSource {
    private static final String PROPERTIES_FILE_NAME = "liquibase.properties";
    private String url;
    private String username;
    private String password;

    public DataSource() {
        setDefaultCredentials();
    }

    public DataSource(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    private void setDefaultCredentials() {
        Properties properties = new Properties();

        // loading database configuration properties
        try (InputStream input = DatabaseSchemas.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            if (input == null) {
                throw new DatabaseConfigurationException(String.format("Unable to find '%s' file.%n", PROPERTIES_FILE_NAME));
            }
            properties.load(input);
        } catch (IOException e) {
            throw new DatabaseConfigurationException(e.getMessage());
        }

        this.url = properties.getProperty("url");
        this.username = properties.getProperty("username");
        this.password = properties.getProperty("password");
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
