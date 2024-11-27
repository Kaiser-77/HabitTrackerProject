package com.project.habit_tracker_app.responses;

import com.project.habit_tracker_app.entities.Post;

import java.util.List;

public record PostPageResponse(List<Post> postList,
                               Integer pageNumber,
                               Integer pageSize,
                               long totalElements,
                               int totalPages,
                               boolean isLast) {
}
