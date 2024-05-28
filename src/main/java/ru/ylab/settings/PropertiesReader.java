package ru.ylab.settings;

import ru.ylab.migrations.DatabaseSchemas;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertiesReader {
    private static final String PROPERTIES_FILE_NAME = "liquibase.properties";

    public PropertyDTO read() {
        Properties properties = new Properties();

        try (InputStream input = DatabaseSchemas.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            if (input == null) {
                throw new RuntimeException(String.format("Unable to find %s file.%n", PROPERTIES_FILE_NAME));
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new PropertyDTO(
            properties.getProperty("url"),
            properties.getProperty("username"),
            properties.getProperty("password"),
            properties.getProperty("driver"),
            properties.getProperty("changelogFile"),
            properties.getProperty("defaultSchemaName"),
            properties.getProperty("liquibaseSchemaName")
        );
    }
}
