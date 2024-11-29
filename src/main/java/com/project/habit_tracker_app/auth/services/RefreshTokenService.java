package com.project.habit_tracker_app.auth.services;


import com.project.habit_tracker_app.auth.entities.RefreshToken;
import com.project.habit_tracker_app.auth.entities.User;
import com.project.habit_tracker_app.auth.repositories.RefreshTokenRepository;
import com.project.habit_tracker_app.auth.repositories.UserRepository;
import com.project.habit_tracker_app.exceptions.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static com.project.habit_tracker_app.utils.AppConstant.REFRESHTOKEN_EXPIRE_TIME;

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
            long refreshTokenValidity = REFRESHTOKEN_EXPIRE_TIME * 3600 * 1000;  //12hrs
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
                .orElseThrow(() -> new AuthenticationException("Refresh Token not found"));

        if(refToken.getExpirationTime().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(refToken);
            throw new AuthenticationException("Refresh Token expired");
        }
        return refToken;
    }



    // --- ADDITIONAL CODE ---------------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        // Delete the refresh token from the database
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }

}

