package com.project.habit_tracker_app.auth.AuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // Determine the error message based on the exception or request context
        String errorMessage;
        if (authException.getMessage().contains("Bad credentials")) {
            errorMessage = "Invalid username or password";
        } else if (authException.getMessage().contains("Access is denied")) {
            errorMessage = "You are not authorized to access this resource";
        } else {
            errorMessage = "Authentication failed: " + authException.getMessage();
        }

        // Write the error response
        response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
    }
}