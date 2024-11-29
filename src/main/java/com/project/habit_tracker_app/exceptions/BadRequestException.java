package com.project.habit_tracker_app.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}


//  VALIDATE USER INPUTS AND THROW --BAD REQUESTS--