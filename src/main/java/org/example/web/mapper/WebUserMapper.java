package org.example.web.mapper;

import org.example.domain.model.User;
import org.example.web.model.UserDto;

public class WebUserMapper {
    public UserDto toDto(User user) {
        return new UserDto(user.getUserId(), user.getLogin());
    }
}
