package com.project.habit_tracker_app.auth.services;


import com.project.habit_tracker_app.auth.entities.User;
import com.project.habit_tracker_app.auth.entities.UserRole;
import com.project.habit_tracker_app.auth.repositories.UserRepository;
import com.project.habit_tracker_app.auth.utils.AuthResponse;
import com.project.habit_tracker_app.auth.utils.LoginRequest;
import com.project.habit_tracker_app.auth.utils.RegisterRequest;
import com.project.habit_tracker_app.entities.Profile;
import com.project.habit_tracker_app.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final ProfileRepository profileRepository;

    public AuthResponse register(RegisterRequest registerRequest){
        var user = User.builder()
                .email(registerRequest.getEmail())
                .userName(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);

        Profile profile = new Profile();
        profile.setUser(savedUser); // Associate the Profile with the saved User
        profile.setBio(""); // Optionally set a default bio or other fields
        profileRepository.save(profile);

        var accessToken = jwtService.generateToken(savedUser);
        var refreshToken = refreshTokenService.createrefreshToken(savedUser.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        var user = userRepository.findByEmailOrUserName(loginRequest.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found!!"));
        var accessToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createrefreshToken(loginRequest.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }
}
