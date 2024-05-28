package fixtures;

import ru.ylab.integrations.DataSource;
import ru.ylab.models.User;
import ru.ylab.repositories.UserRepository;


public class DatabaseFixtures {
    public static final Long USER_ADMIN_ID = 1L;
    public static final String USER_ADMIN_NAME = "admin";
    public static final Long USER_NOT_ADMIN_ID = 2L;
    public static final String USER_NOT_ADMIN_NAME = "user";
    public static final String USER_PASSWORD = "password";


    private final UserRepository userRepository;

    public DatabaseFixtures(DataSource dataSource) {
        this.userRepository = new UserRepository(dataSource);
    }

    public void createUsers() {
        User userAdmin = new User();
        userAdmin.setUsername(USER_ADMIN_NAME);
        userAdmin.setPassword(USER_PASSWORD);
        userAdmin.setAdmin(true);
        userRepository.create(userAdmin);

        User userNotAdmin = new User();
        userNotAdmin.setUsername(USER_NOT_ADMIN_NAME);
        userNotAdmin.setPassword(USER_PASSWORD);
        userNotAdmin.setAdmin(false);
        userRepository.create(userNotAdmin);
    }
}
