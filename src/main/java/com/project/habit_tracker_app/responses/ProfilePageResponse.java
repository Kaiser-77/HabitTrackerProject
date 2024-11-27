package com.project.habit_tracker_app.responses;

import com.project.habit_tracker_app.dto.ProfileDto;

import java.util.List;

public record ProfilePageResponse(List<ProfileDto> profileDtoList,
                                  Integer pageNumber,
                                  Integer pageSize,
                                  long totalElements,
                                  int totalPages,
                                  boolean isLast) {
}
