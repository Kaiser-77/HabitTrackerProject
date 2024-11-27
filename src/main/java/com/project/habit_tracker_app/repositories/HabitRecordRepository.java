package com.project.habit_tracker_app.repositories;

import com.project.habit_tracker_app.entities.Habit;
import com.project.habit_tracker_app.entities.HabitRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitRecordRepository extends JpaRepository<HabitRecord, Long> {

    HabitRecord findTopByHabitOrderByRecordDateDesc(Habit habit);
}
