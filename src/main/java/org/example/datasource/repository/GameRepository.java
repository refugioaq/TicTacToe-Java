package org.example.datasource.repository;

import org.example.datasource.model.GameEntity;
import org.example.web.model.LeaderboardEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GameRepository extends CrudRepository<GameEntity, UUID> {
    @Query("SELECT g FROM GameEntity g WHERE (g.status = 'DRAW') OR (g.winner = :userId)")
    List<GameEntity> findCompletedGamesByUserId(@Param("userId") UUID userId);

    @Query(value = """
            SELECT
            u.user_id AS user_id, u.login AS login,
            CONCAT(
            COUNT(CASE WHEN u.user_id = g.winner THEN 1 END), '/',
            COUNT(CASE WHEN u.user_id != g.winner AND u.user_id = g.first_player OR u.user_id = g.second_player THEN 1 END))
            AS win_rate
            FROM users u
            LEFT JOIN games g ON u.user_id = g.first_player OR u.user_id = g.second_player
            GROUP BY u.user_id, u.login
            ORDER BY COUNT(CASE WHEN u.user_id = g.winner THEN 1 END) DESC,
            COUNT(CASE WHEN u.user_id != g.winner THEN 1 END) ASC
            LIMIT :top
            """, nativeQuery = true)
    List<Object[]> getTop(@Param("top") int top);
}
