package com.project.habit_tracker_app.controllers;

import com.project.habit_tracker_app.auth.entities.RefreshToken;
import com.project.habit_tracker_app.auth.entities.User;
import com.project.habit_tracker_app.auth.services.AuthService;
import com.project.habit_tracker_app.auth.services.JwtService;
import com.project.habit_tracker_app.auth.services.RefreshTokenService;
import com.project.habit_tracker_app.auth.utils.AuthResponse;
import com.project.habit_tracker_app.auth.utils.LoginRequest;
import com.project.habit_tracker_app.auth.utils.RefreshTokenRequest;
import com.project.habit_tracker_app.auth.utils.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")// -------------------- REQUEST HEADER-------------------------
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){

        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = refreshToken.getUser();

        String accessToken = jwtService.generateToken(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build());
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken,
                                    @RequestHeader("X-Refresh-Token") String refreshToken) {

        String accessToken = jwtService.extractToken(bearerToken);
        jwtService.blacklistAccessToken(accessToken);
        refreshTokenService.deleteRefreshToken(refreshToken);

        return ResponseEntity.ok("Logged out successfully");
    }
}

