package org.example.di;

import org.example.datasource.mapper.GameMapper;
import org.example.datasource.repository.GameRepository;
import org.example.datasource.repository.UserRepository;
import org.example.domain.model.GameMode;
import org.example.domain.model.GameModeStrategy;
import org.example.domain.service.*;
import org.example.web.mapper.WebGameMapper;
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
            GameMapper mapper,
            Map<GameMode, GameModeStrategy> strategies
    ) {
        return new GameManagementService(repository, user, service, mapper, strategies);
    }

    @Bean
    public ComputerGameStrategy computerStrategy(
            GameService gameService,
            GameRepository gameRepository,
            GameMapper gameMapper) {
        return new ComputerGameStrategy(gameService, gameRepository, gameMapper);
    }

    @Bean
    public HumanGameStrategy humanStrategy(
            GameService gameService,
            GameRepository gameRepository,
            GameMapper gameMapper) {
        return new HumanGameStrategy(gameService, gameRepository, gameMapper);
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
    public WebGameMapper webGameMapper() {
        return new WebGameMapper();
    }

    @Bean
    public AuthFilter authFilter(UserService service) {
        return new AuthFilter(service);
    }
}