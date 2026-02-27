package org.example.datasource.repository;

import org.example.datasource.model.GameEntity;
import org.springframework.data.repository.CrudRepository;
import java.util.UUID;

public interface GameRepository extends CrudRepository<GameEntity, UUID> {}
