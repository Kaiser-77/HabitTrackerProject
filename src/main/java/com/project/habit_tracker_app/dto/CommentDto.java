package com.project.habit_tracker_app.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {

    private Long id;
    private String userName;
    private String profilePic;
    private String commentText;
    private LocalDateTime createdAt;

    public CommentDto(Long id, String userName,String profilePic, String commentText, LocalDateTime createdAt) {
        this.id = id;
        this.userName = userName;
        this.profilePic = profilePic;
        this.commentText = commentText;
        this.createdAt = createdAt;
    }

    // Getters and setters
}
