package extensions;

import fixtures.DatabaseFixtures;
import liquibase.command.CommandScope;
import liquibase.exception.LiquibaseException;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.ylab.integrations.DataSource;
import ru.ylab.migrations.DatabaseSchemas;
import ru.ylab.settings.PropertiesReader;
import ru.ylab.settings.PropertyDTO;

import org.junit.jupiter.api.extension.Extension;


public class TestDatabaseExtension implements Extension {
    public static final DataSource DATA_SOURCE;

    static {
        PostgreSQLContainer<?> container = PostgreSQLContainerSingleton.getInstance();

        DATA_SOURCE = new DataSource(
            container.getJdbcUrl(),
            container.getUsername(),
            container.getPassword()
        );

        createSchemas(container);
        try {
            applyMigrations(container);
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }
        createFixtures();
    }

    private static void createSchemas(PostgreSQLContainer<?> container) {
        DatabaseSchemas databaseSchemas = new DatabaseSchemas(DATA_SOURCE);
        databaseSchemas.createSchemas();
        String urlWithSchema = String.format("%s?currentSchema=%s", container.getJdbcUrl(), databaseSchemas.getDefaultSchemaName());
        container.withUrlParam("url", urlWithSchema);
    }

    private static void applyMigrations(PostgreSQLContainer<?> container) throws LiquibaseException {
        PropertiesReader propertiesReader = new PropertiesReader();
        PropertyDTO propertyDTO = propertiesReader.read();

        CommandScope liquibaseUpdate = new CommandScope("update");
        liquibaseUpdate.addArgumentValue("changeLogFile", propertyDTO.changelogFile());
        liquibaseUpdate.addArgumentValue("url", container.getJdbcUrl());
        liquibaseUpdate.addArgumentValue("username", container.getUsername());
        liquibaseUpdate.addArgumentValue("password", container.getPassword());
        liquibaseUpdate.execute();
    }

    private static void createFixtures() {
        DatabaseFixtures databaseFixtures = new DatabaseFixtures(DATA_SOURCE);
        databaseFixtures.createUsers();
    }
}
