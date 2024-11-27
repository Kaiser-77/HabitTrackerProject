package com.project.habit_tracker_app.responses;

import com.project.habit_tracker_app.dto.CommentDto;

import java.util.List;

public record CommentPageResponse(List<CommentDto> commentDtoList,
                                  Integer pageNumber,
                                  Integer pageSize,
                                  long totalElements,
                                  int totalPages,
                                  boolean isLast) {
}
