package ru.ylab.services;

import ru.ylab.app.Application;
import ru.ylab.dto.UserDTO;
import ru.ylab.exceptions.ObjectExistsException;
import ru.ylab.models.User;
import ru.ylab.repositories.UserRepository;

import java.util.Optional;


public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository(Application.dataSource);
    }

    /**
     * Register a new user.
     */
    public void registerUser(String username, String password, boolean isAdmin) throws ObjectExistsException {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ObjectExistsException("User already exists.");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setAdmin(isAdmin);
        userRepository.create(user);

        Application.getLogger().log("User registered.");
    }

    /**
     * Log in a user.
     */
    public boolean loginUser(String username, String password) {
        Optional<UserDTO> user = userRepository.findByUsername(username);
        if (user.isPresent() && user.get().password().equals(password)) {
            Application.getUserSession().authenticate(user.get());

            Application.getLogger().log("User logged in.");
            return true;
        }
        return false;
    }

    /**
     * Log out the current user.
     */
    public void logout() {
        Application.getLogger().log("User logged out.");
        Application.getUserSession().logout();
    }
}
