package ru.ylab.auth;

import lombok.Getter;
import ru.ylab.dto.UserDTO;

public class UserSession {
    @Getter
    private UserDTO loggedInUser;

    public void authenticate(UserDTO user) {
        loggedInUser = user;
    }

    public void logout() {
        loggedInUser = null;
    }

    /**
     * Check if a user is authenticated.
     */
    public boolean isAuthenticated() {
        return loggedInUser != null;
    }
}
