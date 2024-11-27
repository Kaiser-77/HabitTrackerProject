package com.project.habit_tracker_app.repositories;

import com.project.habit_tracker_app.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT p.id, p.user.userName,p.profilePic, c.comment, c.createdAt FROM Comment c JOIN Profile p ON c.userCommentedId = p.id WHERE c.postId = :postId")
    List<Object[]> findPostComments(@Param("postId") Long postId);
    @Query("SELECT p.id, p.user.userName,p.profilePic, c.comment, c.createdAt FROM Comment c JOIN Profile p ON c.userCommentedId = p.id WHERE c.postId = :postId")
    Page<Object[]> findPostComments(@Param("postId") Long postId, Pageable pageable);

    void deleteByPostId(Long postId);
    void deleteByPostIdIn(Set<Long> postIds);

    // when Profile delete occurs
    void deleteByUserCommentedId(Long profileId);

}
