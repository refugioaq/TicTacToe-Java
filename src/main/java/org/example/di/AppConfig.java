package org.example.di;

import org.example.datasource.mapper.GameMapper;
import org.example.datasource.repository.GameRepository;
import org.example.datasource.repository.UserRepository;
import org.example.domain.service.*;
import org.example.web.mapper.WebGameMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public GameService gameService() {
        return new GameServiceImpl();
    }

    @Bean
    public GameManagementService gameManagementService(
            GameRepository repository,
            GameService service,
            GameMapper mapper) {
        return new GameManagementService(repository, service, mapper);
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