package com.project.habit_tracker_app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "followTable", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "followed_id"})})
public class Follower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
    private Long userId;

//    @ManyToOne
//    @JoinColumn(name = "followed_id", nullable = false)
    private Long followedId;

    private LocalDateTime createdAt;



// Getters and Setters
}