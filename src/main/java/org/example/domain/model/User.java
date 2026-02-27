package org.example.domain.model;

import java.util.UUID;

public class User {
    private final UUID userId;
    private final String login;
    private final String password;

    public User(String login, String password) {
        this.userId = UUID.randomUUID();
        this.login = login;
        this.password = password;
    }

    public User(UUID userId, String login, String password) {
        this.userId = userId;
        this.login = login;
        this.password = password;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
