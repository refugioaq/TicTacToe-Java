package org.example.domain.service;

import org.example.datasource.model.UserEntity;
import org.example.datasource.repository.UserRepository;
import org.example.domain.exception.InvalidPasswordException;
import org.example.domain.exception.UserNotFoundException;
import org.example.web.model.SignUpRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public boolean register(SignUpRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            return false;
        }
        UserEntity entity = new UserEntity(UUID.randomUUID(), request.getLogin(), request.getPassword());
        userRepository.save(entity);
        return true;
    }

    @Override
    public UUID authorize(String base64) {
        String decoded = new String(Base64.getDecoder().decode(base64));

        String[] data = decoded.split(":");
        String login = data[0];
        String password = data[1];

        if (!userRepository.existsByLogin(login)) {
            return null;
        }

        UserEntity entity = userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(login));
        
        if (!password.equals(entity.getPassword())) {
            throw new InvalidPasswordException();
        }

        return entity.getUserId();
    }
}
