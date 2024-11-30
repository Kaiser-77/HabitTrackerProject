package com.project.habit_tracker_app.auth.utils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Email can't be empty")
    private String email;

    @NotBlank(message = "username can't be empty")
    private String username;

    @NotBlank(message = "password can't be empty")
    @Size(min = 5, message = "minimum 5 characters")
    private String password;
}
