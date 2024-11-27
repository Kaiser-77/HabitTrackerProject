package com.project.habit_tracker_app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileModificationDto {

    private String name;
    private int age;
    private String bio;
    private String profilePic;

    //AllArgsConstructor
    public ProfileModificationDto(String name, int age, String bio, String profilePic) {
    }

    // Getters and Setters
}