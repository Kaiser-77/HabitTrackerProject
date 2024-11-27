package com.project.habit_tracker_app.auth.services;


import com.project.habit_tracker_app.auth.entities.RefreshToken;
import com.project.habit_tracker_app.auth.entities.User;
import com.project.habit_tracker_app.auth.repositories.RefreshTokenRepository;
import com.project.habit_tracker_app.auth.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createrefreshToken(String username){
        User user = userRepository.findByEmailOrUserName(username).orElseThrow(() -> new UsernameNotFoundException("User not found with this Email: " + username));
        RefreshToken refreshToken = user.getRefreshToken();

        if(refreshToken == null){
            long refreshTokenValidity = 43200*1000;  //12hrs
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                    .user(user)
                    .build();

            refreshTokenRepository.save(refreshToken);
        }
        return refreshToken;
    }


    public RefreshToken verifyRefreshToken(String refreshToken){
        RefreshToken refToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh Token not found"));

        if(refToken.getExpirationTime().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(refToken);
            throw new RuntimeException("Refresh Token expired");
        }
        return refToken;
    }
}

