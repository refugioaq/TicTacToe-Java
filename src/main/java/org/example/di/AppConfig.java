package org.example.di;

import org.example.datasource.mapper.GameMapper;
import org.example.datasource.mapper.UserMapper;
import org.example.datasource.repository.GameRepository;
import org.example.datasource.repository.UserRepository;
import org.example.domain.model.GameMode;
import org.example.domain.service.security.JwtProvider;
import org.example.domain.service.security.JwtUtil;
import org.example.domain.service.strategy.GameModeStrategy;
import org.example.domain.service.*;
import org.example.domain.service.gameService.GameService;
import org.example.domain.service.gameService.GameServiceImpl;
import org.example.domain.service.strategy.ComputerGameStrategy;
import org.example.domain.service.strategy.HumanGameStrategy;
import org.example.web.mapper.WebGameMapper;
import org.example.web.mapper.WebUserMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class AppConfig {

    @Bean
    public GameService gameService() {
        return new GameServiceImpl();
    }

    @Bean
    public GameManagementService gameManagementService(
            GameRepository repository,
            UserRepository user,
            GameService service,
            GameMapper gameMapper,
            UserMapper userMapper,
            Map<GameMode, GameModeStrategy> strategies
    ) {
        return new GameManagementService(repository, user, service, gameMapper, userMapper, strategies);
    }

    @Bean
    public ComputerGameStrategy computerStrategy(
            GameService gameService,
            GameRepository gameRepository,
            GameMapper gameMapper,
            UserRepository userRepository) {
        return new ComputerGameStrategy(gameService, gameRepository, gameMapper, userRepository);
    }

    @Bean
    public HumanGameStrategy humanStrategy(
            GameService gameService,
            GameRepository gameRepository,
            GameMapper gameMapper,
            UserRepository userRepository) {
        return new HumanGameStrategy(gameService, gameRepository, gameMapper, userRepository);
    }

    @Bean
    public Map<GameMode, GameModeStrategy> strategyMap(
            ComputerGameStrategy computer,
            HumanGameStrategy human
    ) {
        return Map.of(
            GameMode.COMPUTER, computer,
            GameMode.HUMAN, human);
    }

    @Bean
    public GameMapper dataSourceGameMapper() {
        return new GameMapper();
    }

    @Bean
    public UserMapper dataSourceUserMapper() {
        return new UserMapper();
    }

    @Bean
    public WebGameMapper webGameMapper() {
        return new WebGameMapper();
    }

    @Bean
    public WebUserMapper webUserMapper() {
        return new WebUserMapper();
    }

    @Bean
    public JwtProvider jwtProvider() { return new JwtProvider(); }

    @Bean
    public JwtUtil jwtUtil() { return new JwtUtil(); }

    @Bean
    public AuthFilter authFilter(JwtProvider jwtProvider, JwtUtil jwtUtil) {
        return new AuthFilter(jwtProvider, jwtUtil);
    }
}