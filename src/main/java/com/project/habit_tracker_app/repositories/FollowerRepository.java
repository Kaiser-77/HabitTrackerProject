package com.project.habit_tracker_app.repositories;

import com.project.habit_tracker_app.entities.Follower;
import com.project.habit_tracker_app.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FollowerRepository extends JpaRepository<Follower, Long> {

    @Query("SELECT f.followedId FROM Follower f WHERE f.userId = :userId")
    List<Long> findFollowingProfileIds(@Param("userId") Long userId);

    // Retrieve IDs of users who are following a specific profile
    @Query("SELECT f.userId FROM Follower f WHERE f.followedId = :followedId")
    List<Long> findFollowerProfileIds(@Param("followedId") Long followedId);


    // Find a follower relationship by user ID and followed ID
    Optional<Follower> findByUserIdAndFollowedId(Long userId, Long followedId);

    // Delete a follower relationship by user ID and followed ID
    @Transactional
    void deleteByUserIdAndFollowedId(Long userId, Long followedId);

    // when profile delete occurs
    @Modifying
    @Query("DELETE FROM Follower f WHERE f.userId = :profileId OR f.followedId = :profileId")
    void deleteByUserIdOrFollowedId(@Param("profileId") Long profileId);


}
