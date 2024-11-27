package com.project.habit_tracker_app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "post_id", nullable = false)
//    @JsonBackReference
    private Long postId;


//    @ManyToOne
//    @JoinColumn(name = "profile_id", nullable = false)
    private Long userLikedId;

    private LocalDateTime createdAt;

    // Getters and Setters
}
