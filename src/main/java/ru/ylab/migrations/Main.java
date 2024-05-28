package ru.ylab.migrations;

import ru.ylab.integrations.DataSource;

public class Main {
    public static void main(String[] args) {
        DataSource dataSource = new DataSource();
        new DatabaseSchemas(dataSource).createSchemas();
    }
}
