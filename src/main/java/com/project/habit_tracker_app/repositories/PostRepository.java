package com.project.habit_tracker_app.repositories;

import com.project.habit_tracker_app.entities.Post;
import com.project.habit_tracker_app.entities.Privacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByProfileId(Long profileId);
    Page<Post> findByProfileId(Long profileId, Pageable pageable);



    List<Post> findByProfileIdAndPrivacy(Long profileId, Privacy privacy);
    Page<Post> findByProfileIdAndPrivacy(Long profileId, Privacy privacy, Pageable pageable);

    List<Post> findByProfileIdInAndPrivacy(List<Long> profileIds, Privacy privacy);
    Page<Post> findByProfileIdInAndPrivacy(List<Long> profileIds, Privacy privacy, Pageable pageable);



    // WHEN PROFILE DELETE OCCURS
    void deleteByProfileId(Long profileId);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount - 1 WHERE p.id IN (SELECT l.postId FROM Likes l WHERE l.userLikedId = :profileId)")
    void decrementLikesCountByProfileId(Long profileId);

    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount - 1 WHERE p.id IN (SELECT c.postId FROM Comment c WHERE c.userCommentedId = :profileId)")
    void decrementCommentCountByProfileId(Long profileId);
}
