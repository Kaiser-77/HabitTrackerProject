package com.project.habit_tracker_app.controllers;


import com.project.habit_tracker_app.entities.Likes;
import com.project.habit_tracker_app.services.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final PostService postService;

    public AdminController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> check(){
        return ResponseEntity.ok("Hello...!!");

    }

    @GetMapping("/allLikes")
    public ResponseEntity<List<Likes>> getALlLikes(){
        return ResponseEntity.ok(postService.getAllLikes());
    }
}
