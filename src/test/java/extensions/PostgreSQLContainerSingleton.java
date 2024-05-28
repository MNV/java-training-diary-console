package extensions;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgreSQLContainerSingleton {

    private static final PostgreSQLContainer<?> CONTAINER;

    static {
        CONTAINER = new PostgreSQLContainer<>("postgres:16.3-alpine");
        CONTAINER.start();
    }

    public static PostgreSQLContainer<?> getInstance() {
        return CONTAINER;
    }
}
