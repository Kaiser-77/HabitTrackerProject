package com.project.habit_tracker_app.services;

import com.project.habit_tracker_app.dto.HabitDto;
import com.project.habit_tracker_app.dto.HabitModificationDto;
import com.project.habit_tracker_app.responses.HabitPageResponse;
import com.project.habit_tracker_app.entities.*;
import com.project.habit_tracker_app.repositories.HabitRecordRepository;
import com.project.habit_tracker_app.repositories.HabitRepository;
import com.project.habit_tracker_app.repositories.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.project.habit_tracker_app.utils.AppConstant.SORT_BY_TIME;

@Service
public class HabitService {
    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private HabitRecordRepository habitRecordRepository;

    @Autowired
    private ProfileRepository profileRepository;

    public List<Habit> getAllHabits() {
        return habitRepository.findAll();
    }


    public Habit getHabitById(Long habitId){
        return habitRepository.findById(habitId).orElseThrow(()-> new RuntimeException("no such habit found"));
    }


    public HabitPageResponse getUsersHabit(Long userId, Long ownerId, Integer pageNumber, Integer pageSize, String sortBy, String dir){

//        Sort sort = dir.equalsIgnoreCase("asc")
//                ? Sort.by(sortBy).ascending()
//                : Sort.by(sortBy).descending();
        Sort sort = dir.equalsIgnoreCase("asc")
                ? Sort.by(Sort.Order.asc(sortBy), Sort.Order.asc(SORT_BY_TIME))   // Secondary sort by 'name' in ascending
                : Sort.by(Sort.Order.desc(sortBy), Sort.Order.desc(SORT_BY_TIME));

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Habit> habitPages;


        // If the viewer is the owner, show all habits (public and private)
        if (userId.equals(ownerId)) {
            habitPages = habitRepository.findByProfileId(userId, pageable);
        }
        // If the viewer is not the owner, only show public habits
        else {
            habitPages = habitRepository.findByProfileIdAndPrivacy(userId, Privacy.PUBLIC, pageable);
        }

        List<Habit> habits = habitPages.getContent();

        List<HabitDto> habitDtoList = new ArrayList<>();
        for(Habit habit : habits){
            HabitDto habitDto = new HabitDto(
                    habit.getId(),
                    habit.getName(),
                    habit.getCurrentStreak(),
                    habit.getPrivacy(),
                    habit.getPriority()
                    );
            habitDtoList.add(habitDto);
        }

        return new HabitPageResponse(habitDtoList, pageNumber, pageSize,
                habitPages.getTotalElements(),
                habitPages.getTotalPages(),
                habitPages.isLast());

    }

//NEED TO REWRITE------------------------------------------------------------------------------------------------------------------
    public Habit addHabit(HabitModificationDto habitModificationDto, Long ownerId) {
        Profile profile = profileRepository.findById(ownerId).orElseThrow(()-> new RuntimeException("profile not found"));
        Habit habit = new Habit();
        habit.setPrivacy(habitModificationDto.getPrivacy());
        habit.setName(habitModificationDto.getName());
        habit.setDescription(habitModificationDto.getDescription());
        habit.setCurrentStreak(0);
        habit.setBestStreak(0);
        habit.setProfileId(ownerId);
        habit.setCreatedAt(LocalDateTime.now());
        habit.setPriority(habitModificationDto.getPriority());
        Habit savedHabit = habitRepository.save(habit);
        profile.getHabitIds().add(savedHabit.getId());
        profileRepository.save(profile);
        return savedHabit;
    }

    public Habit updateHabit(Long habitId, HabitModificationDto updatedHabit, Long ownerId) {
        // Find the existing habit by ID
        Habit habit = habitRepository.findById(habitId).orElseThrow(()-> new RuntimeException("no such habit or task found"));

        if (habit.getProfileId().equals(ownerId)) {

            habit.setName(updatedHabit.getName());
            habit.setDescription(updatedHabit.getDescription());
            habit.setPrivacy(updatedHabit.getPrivacy());                //add additioinal specs
            habit.setPriority(updatedHabit.getPriority());

            return habitRepository.save(habit);
        } else {
            throw new RuntimeException("can't edit other people's habit");
        }
    }

    
    public void deleteHabit(Long habitId, Long ownerId) {
        Habit habit = habitRepository.findById(habitId).orElseThrow(()-> new EntityNotFoundException("habit not found"));
        Profile profile = profileRepository.findById(habit.getProfileId()).orElseThrow(()->new RuntimeException("no such profile found with habit"));
        if (habit.getProfileId().equals(ownerId)) {
            profile.getHabitIds().remove(habitId);
            habitRepository.delete(habit);
            profileRepository.save(profile);
        } else{
            throw new RuntimeException("can't delete other's task or habit");
        }
    }



    @Transactional
    public void updateStreakAndAddRecord(Long habitId, Long ownerId) {

        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        if(!(habit.getProfileId().equals(ownerId))){
            throw new RuntimeException("cant tick others profile");
        }
        // Get the latest record if exists
        HabitRecord latestRecord = habitRecordRepository.findTopByHabitOrderByRecordDateDesc(habit);
        LocalDateTime today = LocalDateTime.now();

        //check if today's record exist or not
        boolean recordExistsToday = latestRecord != null &&
                latestRecord.getRecordDate().toLocalDate().isEqual(today.toLocalDate());
        if (recordExistsToday) {
            // If a record for today already exists, do nothing or throw a message
            System.out.println("Record for today already exists.");
            return;
        }

        // Check if latest record was yesterday
        boolean isConsecutive = latestRecord != null &&
                latestRecord.getRecordDate().toLocalDate().plusDays(1).isEqual(today.toLocalDate());

        // Update streak
        if (isConsecutive) {
            habit.setCurrentStreak(habit.getCurrentStreak() + 1);
        } else {
            habit.setCurrentStreak(1); // Reset streak
        }

        // Update best streak if needed
        if (habit.getCurrentStreak() > habit.getBestStreak()) {
            habit.setBestStreak(habit.getCurrentStreak());
        }

        // Save the new record
        HabitRecord newRecord = new HabitRecord();
        newRecord.setHabit(habit);
        newRecord.setRecordDate(today);
        habitRecordRepository.save(newRecord);

        // Save updated habit
        habitRepository.save(habit);
    }


    public List<HabitRecord> getHabitRecords(Long habitId){
        Habit habit = habitRepository.findById(habitId).orElseThrow(()-> new RuntimeException("Habit not found"));
        return habit.getRecords();
    }
}