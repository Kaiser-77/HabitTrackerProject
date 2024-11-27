package com.project.habit_tracker_app.responses;

import com.project.habit_tracker_app.dto.HabitDto;

import java.util.List;

public record HabitPageResponse(List<HabitDto> habitDtoList,
                                Integer pageNumber,
                                Integer pageSize,
                                long totalElements,
                                int totalPages,
                                boolean isLast) {
}
