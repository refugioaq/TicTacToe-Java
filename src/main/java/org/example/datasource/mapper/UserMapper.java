package org.example.datasource.mapper;

import org.example.datasource.model.UserEntity;
import org.example.domain.model.User;

public class UserMapper {
    public UserEntity toEntity(User user) {
        if (user == null) return null;

        return new UserEntity(user.getUserId(), user.getLogin(), user.getPassword());
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return new User(entity.getUserId(), entity.getLogin(), entity.getPassword());
    }
}
