package org.example.datasource.mapper;

import org.example.datasource.model.UserEntity;
import org.example.domain.model.Role;
import org.example.domain.model.User;

public class UserMapper {
    public UserEntity toEntity(User user) {
        if (user == null) return null;

        return new UserEntity(user.userId(), user.login(), user.password(), convertRoleToString(user.role()), user.refreshToken());
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return new User(entity.getUserId(), entity.getLogin(), entity.getPassword(), convertStringToRole(entity.getRole()), entity.getRefreshToken());
    }

    private String convertRoleToString(Role role) {
        return role != null ? role.name() : Role.USER.name();
    }

    private Role convertStringToRole(String string) {
        try {
            return Role.valueOf(string);
        } catch (IllegalArgumentException e) {
            return Role.USER;
        }
    }
}
