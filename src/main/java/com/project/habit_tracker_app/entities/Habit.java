package com.project.habit_tracker_app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Entity
@Getter
@Setter
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "profile_id")
//    @JsonIgnore
    private Long profileId;

    private String name;
    private String description;
    private int currentStreak;
    private int bestStreak;

    @Enumerated(EnumType.STRING)
    private Privacy privacy;

    private int priority = 0;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<HabitRecord> records = new ArrayList<>();

    // Getters and Setters
}