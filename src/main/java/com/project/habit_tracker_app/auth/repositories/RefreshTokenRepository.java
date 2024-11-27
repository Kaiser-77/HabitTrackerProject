package com.project.habit_tracker_app.auth.repositories;

import com.project.habit_tracker_app.auth.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
