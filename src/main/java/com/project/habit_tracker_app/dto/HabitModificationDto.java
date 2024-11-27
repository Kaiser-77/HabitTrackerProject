package com.project.habit_tracker_app.dto;

import com.project.habit_tracker_app.entities.Privacy;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
public class HabitModificationDto {

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private Privacy privacy;

    private int priority;




    // Getters and setters
}