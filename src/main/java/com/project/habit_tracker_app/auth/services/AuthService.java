package com.project.habit_tracker_app.auth.services;


import com.project.habit_tracker_app.auth.entities.PendingUser;
import com.project.habit_tracker_app.auth.entities.User;
import com.project.habit_tracker_app.auth.entities.UserRole;
import com.project.habit_tracker_app.auth.repositories.PendingUserRepository;
import com.project.habit_tracker_app.auth.repositories.UserRepository;
import com.project.habit_tracker_app.auth.utils.AuthResponse;
import com.project.habit_tracker_app.auth.utils.LoginRequest;
import com.project.habit_tracker_app.auth.utils.RegisterRequest;
import com.project.habit_tracker_app.dto.MailBody;
import com.project.habit_tracker_app.entities.Profile;
import com.project.habit_tracker_app.exceptions.ValidationException;
import com.project.habit_tracker_app.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final ProfileRepository profileRepository;
    private final PendingUserRepository pendingUserRepository;

    public String register(RegisterRequest registerRequest){

        Optional<User> optionalUser1 = userRepository.findByEmail(registerRequest.getEmail());
        if(optionalUser1.isPresent()) throw new ValidationException("This email already has ann account");
        Optional<User> optionalUser2 = userRepository.findByUserName(registerRequest.getUsername());
        if(optionalUser2.isPresent()) throw new ValidationException("This username already user");

        String verificationToken = UUID.randomUUID().toString();

        PendingUser pendingUser = pendingUserRepository.findByEmail(registerRequest.getEmail()).orElse(new PendingUser());
        pendingUser.setEmail(registerRequest.getEmail());
        pendingUser.setUsername(registerRequest.getUsername());
        pendingUser.setPassword(registerRequest.getPassword());
        pendingUser.setOtp(verificationToken);
        pendingUser.setExpirationTime(new Date(System.currentTimeMillis() + 300 * 1000));   //  5 min
        pendingUserRepository.save(pendingUser);

        MailBody mailBody = MailBody.builder()
                .to(registerRequest.getEmail())
                .subject("Verify your email")
                .text("Click the link to verify your account: http://localhost:8080/api/auth/verifyRegistration/" + verificationToken + "?email=" + registerRequest.getEmail())
                .build();

        emailService.sendSimpleMessage(mailBody);

        return "OTP sent to email for verification";

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




    public AuthResponse verifyRegistration(String otp) {

        PendingUser pendingUser = pendingUserRepository.findByOtp(otp).orElseThrow(() -> new RuntimeException("Invalid OTP "));

        if (pendingUser.getExpirationTime().before(Date.from(Instant.now()))) {
            pendingUserRepository.delete(pendingUser);
            throw new ValidationException("The OTP has expired.");
        }

        User user = User.builder()
                .email(pendingUser.getEmail())
                .userName(pendingUser.getUsername())
                .password(passwordEncoder.encode(pendingUser.getPassword()))
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        Profile profile = new Profile();
        profile.setUser(savedUser); // Associate the Profile with the saved User
        profile.setBio(""); // Optionally set a default bio or other fields
        profileRepository.save(profile);

        pendingUserRepository.deleteById(pendingUser.getId());

        var accessToken = jwtService.generateToken(savedUser);
        var refreshToken = refreshTokenService.createrefreshToken(savedUser.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }
}
