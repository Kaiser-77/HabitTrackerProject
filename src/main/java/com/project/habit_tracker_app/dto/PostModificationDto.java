package com.project.habit_tracker_app.dto;

import com.project.habit_tracker_app.entities.Privacy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostModificationDto {


    private String caption;

    @Enumerated(EnumType.STRING)
    private Privacy privacy;



    //AllArgsConstructor
    public PostModificationDto(String caption, Privacy privacy) {
    }

    // Getters and Setters
}