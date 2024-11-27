package com.project.habit_tracker_app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "post_id")
//    @JsonBackReference
    private Long postId;

//    @ManyToOne
//    @JoinColumn(name = "profile_id")
    private Long userCommentedId;

    private String comment;

    private LocalDateTime createdAt;

    // Getters and Setters
}