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
public class HabitDto {

    private Long id;
    private String name;
    private int currentStreak;

    @Enumerated(EnumType.STRING)
    private Privacy privacy;

    private int priority;




    // Getters and setters
}