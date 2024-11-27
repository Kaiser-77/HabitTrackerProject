package com.project.habit_tracker_app.repositories;

import com.project.habit_tracker_app.entities.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Profile findByName(String name);

    @Transactional
    List<Profile> findByNameContainingIgnoreCase(String name);
    @Transactional
    Page<Profile> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Transactional
    @Query("SELECT p FROM Profile p  WHERE LOWER(p.user.userName) LIKE LOWER(CONCAT('%', :username, '%'))")
    Page<Profile> findByUsernameContainingIgnoreCase(@Param("username") String username, Pageable pageable);





    @Modifying
    @Query("UPDATE Profile p SET p.following = p.following - 1 WHERE p.id IN (SELECT f.userId FROM Follower f WHERE f.followedId = :profileId)")
    void decrementFollowingCountByProfileId(Long profileId);


    @Modifying
    @Query("UPDATE Profile p SET p.followers = p.followers - 1 WHERE p.id IN (SELECT f.followedId FROM Follower f WHERE f.userId = :profileId)")
    void decrementFollowersCountByProfileId(Long profileId);



    Page<Profile> findByIdIn(List<Long> profileIds, Pageable pageable);
}
