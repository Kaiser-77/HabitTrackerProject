package com.project.habit_tracker_app.controllers;


import com.project.habit_tracker_app.auth.entities.ForgotPassword;
import com.project.habit_tracker_app.auth.entities.User;
import com.project.habit_tracker_app.auth.repositories.ForgotPasswordRepository;
import com.project.habit_tracker_app.auth.repositories.UserRepository;
import com.project.habit_tracker_app.auth.services.EmailService;
import com.project.habit_tracker_app.auth.utils.ChangePassword;
import com.project.habit_tracker_app.dto.MailBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    private  final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordController(UserRepository userRepository, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // send mail for email verification
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Please provide valid email"));

        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This the OTP for your Forgot Password Request : " + otp)
                .subject("OTP for forgot password request")
                .build();

        ForgotPassword fp = forgotPasswordRepository.findByUser(user).orElse(new ForgotPassword());
        fp.setOtp(otp);
        fp.setExpirationTime(new Date(System.currentTimeMillis() + 300 * 1000));    //  5 min
        fp.setUser(user);

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok("Email send for verification");
    }


    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Please provide valid email"));
        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user).orElseThrow(() -> new RuntimeException("Invalid OTP for email"));

        if(fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.delete(fp);
            return new ResponseEntity<>("OTP is expired", HttpStatus.EXPECTATION_FAILED);
        }

        fp.setOtpVerified(true);
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok("OTP verified");
    }


    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword, @PathVariable String email){

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Please provide valid email"));
        ForgotPassword fp = forgotPasswordRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Not verified yet"));

        // Check if OTP was verified
        if (!fp.isOtpVerified()) {
            return new ResponseEntity<>("OTP not verified", HttpStatus.FORBIDDEN);
        }

        if(!Objects.equals(changePassword.password(), changePassword.repeatPassword())){
            return  new ResponseEntity<>("Please enter the password again", HttpStatus.EXPECTATION_FAILED);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encodedPassword);
        forgotPasswordRepository.delete(fp);

        return ResponseEntity.ok("Password has been changed!");
    }

    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
