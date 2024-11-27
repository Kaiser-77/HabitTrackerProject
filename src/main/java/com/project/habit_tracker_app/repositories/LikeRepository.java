package com.project.habit_tracker_app.repositories;

import com.project.habit_tracker_app.entities.Likes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LikeRepository extends JpaRepository<Likes, Long> {

    @Query("SELECT p.id, p.user.userName, p.name, p.profilePic FROM Likes l JOIN Profile p ON l.userLikedId = p.id WHERE l.postId = :postId")
    List<Object[]> findPostLikes(@Param("postId") Long postId);
    @Query("SELECT p.id, p.user.userName, p.name, p.profilePic FROM Likes l JOIN Profile p ON l.userLikedId = p.id WHERE l.postId = :postId")
    Page<Object[]> findPostLikes(@Param("postId") Long postId, Pageable pageable);

    // Find a like by post ID and user ID
    Optional<Likes> findByPostIdAndUserLikedId(Long postId, Long likedUserId);

    void deleteByPostId(Long postId);
    void deleteByPostIdIn(Set<Long> postIds);

    // when Profile delete occurs
    void deleteByUserLikedId(Long profileId);

}
