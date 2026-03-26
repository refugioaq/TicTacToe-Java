package org.example.domain.service.userService;

import org.example.datasource.mapper.UserMapper;
import org.example.datasource.model.UserEntity;
import org.example.datasource.repository.UserRepository;
import org.example.domain.exception.InvalidTokenException;
import org.example.domain.exception.UserNotFoundException;
import org.example.domain.model.User;
import org.example.domain.security.JwtAuthentication;
import org.example.domain.service.security.JwtProvider;
import org.example.domain.service.security.JwtUtil;
import org.example.web.model.JwtRequest;
import org.example.web.model.JwtResponse;
import org.example.web.model.SignUpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

import static org.example.domain.Configuration.TYPE;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final JwtUtil util;
    private final UserMapper mapper;
    private final JwtProvider provider;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, JwtUtil util, UserMapper mapper, JwtProvider provider, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.util = util;
        this.mapper = mapper;
        this.provider = provider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public boolean register(SignUpRequest request) {
        if (repository.existsByLogin(request.getLogin())) {
            return false;
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        UserEntity entity = new UserEntity(UUID.randomUUID(), request.getLogin(), hashedPassword, "USER", null);
        repository.save(entity);
        return true;
    }

    @Override
    public JwtResponse login(JwtRequest request) {

        String login = request.login();

        UserEntity entity = repository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(login));

        if (!passwordEncoder.matches(request.password(), entity.getPassword())) {
            throw new UserNotFoundException(login);
        }

        String refreshToken = provider.generateRefreshToken(mapper.toDomain(entity));

        entity.setRefreshToken(refreshToken);
        repository.save(entity);

        return new JwtResponse(TYPE, provider.generateAccessToken(mapper.toDomain(entity)), refreshToken);
    }

    @Override
    public JwtResponse access(String refreshToken) {
        if (!provider.validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException();
        }

        UserEntity entity = repository.findByRefreshToken(refreshToken)
                .orElseThrow(InvalidTokenException::new);

        return new JwtResponse(TYPE,
                provider.generateAccessToken(mapper.toDomain(entity)),
                refreshToken);
    }

    @Override
    public JwtResponse refresh(String refreshToken) {
        if (!provider.validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException();
        }

        UserEntity entity = repository.findByRefreshToken(refreshToken)
                .orElseThrow(InvalidTokenException::new);

        return new JwtResponse(TYPE,
                provider.generateAccessToken(mapper.toDomain(entity)),
                provider.generateRefreshToken(mapper.toDomain(entity)));
    }

    @Override
    public JwtAuthentication getJwtAuthentication() {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthentication jwtAuth) {
            return jwtAuth;
        }
        return null;
    }

    @Override
    public User getUserByAccessToken(UUID userId) {

        UserEntity entity = repository
                .findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        if (!provider.validateRefreshToken(entity.getRefreshToken())) {
            throw new InvalidTokenException();
        }

        return mapper.toDomain(entity);
    }
}
