package ru.ylab.settings;


public record PropertyDTO(
    String dbUrl, String dbUsername, String dbPassword, String dbDriver,
    String changelogFile, String defaultSchemaName, String liquibaseSchemaName
) {}
