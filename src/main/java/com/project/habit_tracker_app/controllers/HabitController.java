package com.project.habit_tracker_app.controllers;

import com.project.habit_tracker_app.auth.utils.AuthenticatedUserUtil;
import com.project.habit_tracker_app.dto.HabitModificationDto;
import com.project.habit_tracker_app.entities.HabitRecord;
import com.project.habit_tracker_app.responses.HabitPageResponse;
import com.project.habit_tracker_app.entities.Habit;
import com.project.habit_tracker_app.services.HabitService;
import com.project.habit_tracker_app.utils.AppConstant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HabitController {


    private final HabitService habitService;
    public final AuthenticatedUserUtil authenticatedUserUtil;

    public HabitController(HabitService habitService, AuthenticatedUserUtil authenticatedUserUtil) {
        this.habitService = habitService;
        this.authenticatedUserUtil = authenticatedUserUtil;
    }




    // Get habits by user
    @GetMapping("/user-habit/{userId}")
    public ResponseEntity<HabitPageResponse> getUserHabits(@PathVariable Long userId,
                                                           @RequestParam(defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
                                                           @RequestParam(defaultValue = AppConstant.PAGE_SIZE_HBT, required = false) Integer pageSize,
                                                           @RequestParam(defaultValue = AppConstant.SORT_BY_PRIORITY, required = false) String sortBy,
                                                           @RequestParam(defaultValue = AppConstant.SORT_DIR, required = false) String dir) {
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        return ResponseEntity.ok(habitService.getUsersHabit(userId,ownerId,pageNumber,pageSize,sortBy,dir));
    }

    // Add a new habit
    @PostMapping("/addHabit")
    public ResponseEntity<Habit> addHabit(@RequestBody HabitModificationDto habitModificationDto) {
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        Habit newHabit = habitService.addHabit(habitModificationDto,ownerId);
        return new ResponseEntity<>(newHabit, HttpStatus.CREATED);
    }

    // Update a habit
    @PutMapping("/update/{habitId}")
    public ResponseEntity<Habit> updateHabit(@PathVariable Long habitId, @RequestBody HabitModificationDto updatedHabit) {
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        Habit habit = habitService.updateHabit(habitId, updatedHabit, ownerId);
        return ResponseEntity.ok(habit);
    }

    // Delete a habit
    @DeleteMapping("/delete/{habitId}")
    public void deleteHabit(@PathVariable Long habitId) {
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        habitService.deleteHabit(habitId, ownerId);
        return ;
    }

    // Update streaks for a habit
    @PostMapping("/{habitId}/update-streaks")
    public String updateStreaks(@PathVariable Long habitId) {
//        Habit habit = habitService.getHabitById(habitId); // Assuming you add a method to fetch habit by ID
//        habitService.updateStreaks(habit);
        Long ownerId = authenticatedUserUtil.getCurrentUserId();
        habitService.updateStreakAndAddRecord(habitId, ownerId);
        return "updated";
    }


    @GetMapping("/getHabitRecords")
    public ResponseEntity<List<HabitRecord>> getHabitRecords(@RequestParam Long habitId) {
        return  ResponseEntity.ok(habitService.getHabitRecords(habitId));
    }
}
