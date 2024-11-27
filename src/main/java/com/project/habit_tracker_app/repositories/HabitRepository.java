package com.project.habit_tracker_app.repositories;

import com.project.habit_tracker_app.entities.Habit;
import com.project.habit_tracker_app.entities.Privacy;
import com.project.habit_tracker_app.entities.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {

    List<Habit> findByProfileId(Long profileId);
    Page<Habit> findByProfileId(Long profileId, Pageable pageable);

    List<Habit> findByProfileIdAndPrivacy(Long profileId, Privacy privacy);
    Page<Habit> findByProfileIdAndPrivacy(Long profileId, Privacy privacy, Pageable pageable);

    // when Profile delete occurs
    void deleteByProfileId(Long profileId);


}
