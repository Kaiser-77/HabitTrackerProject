package com.project.habit_tracker_app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "profile_id")
//    @JsonBackReference                           // TO STOP RECURSIVE SERIALISATION
    private Long profileId;

    private String picture;
    private String caption;

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.EAGER)   // orphanRemoval = true  (REMOVED WHEN LIKES IS DELETED) , fetch = FetchType.EAGER
//    @JsonManagedReference
    private Long likeCount = 0L;

    @Enumerated(EnumType.STRING)
    private Privacy privacy;

    private LocalDateTime createdAt;

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JsonManagedReference
    private Long commentCount = 0L;

    // Getters and Setters
}