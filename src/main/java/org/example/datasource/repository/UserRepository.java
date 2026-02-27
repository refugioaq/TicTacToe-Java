package org.example.datasource.repository;

import org.example.datasource.model.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<UserEntity, UUID> {
    boolean existsByLogin(String login);
    Optional<UserEntity> findByLogin(String login);
}
