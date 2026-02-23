package org.example.di;

import org.example.datasource.mapper.GameMapper;
import org.example.datasource.repository.GameRepository;
import org.example.datasource.repository.GameRepositoryImpl;
import org.example.datasource.storage.GameStorage;
import org.example.domain.service.GameManagementService;
import org.example.domain.service.GameService;
import org.example.domain.service.GameServiceImpl;
import org.example.web.mapper.WebGameMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public GameService gameService() {
        return new GameServiceImpl();
    }

    @Bean
    public GameServiceImpl gameServiceImpl() { return new GameServiceImpl(); }

    @Bean
    public GameManagementService GameManagementService(
            GameRepository repository,
            @Qualifier("gameServiceImpl") GameServiceImpl service) {
        return new GameManagementService(repository, service);
    }
    @Bean
    public GameStorage gameStorage() {
        return new GameStorage();
    }

    @Bean
    public GameMapper dataSourceGameMapper() {
        return new GameMapper();
    }

    @Bean
    public GameRepository gameRepository(GameStorage gameStorage, GameMapper mapper) {
        return new GameRepositoryImpl(mapper, gameStorage);
    }

    @Bean
    public WebGameMapper webGameMapper() {
        return new WebGameMapper();
    }
}