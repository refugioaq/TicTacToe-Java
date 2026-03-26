package org.example.datasource.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    private UUID userId;
    @Column
    private String login;
    @Column
    private String  password;

    @Column
    private String role;

    @Column(name = "refresh_token")
    private String refreshToken;

    public UserEntity() {}
    public UserEntity(UUID userId, String login, String password, String role, String refreshToken) {
        this.userId = userId;
        this.login = login;
        this.password = password;
        this.role = role;
        this.refreshToken = refreshToken;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
