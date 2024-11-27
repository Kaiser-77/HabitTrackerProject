package com.project.habit_tracker_app.auth.repositories;

import com.project.habit_tracker_app.auth.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String username);

    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.userName = :identifier")
    Optional<User> findByEmailOrUserName(@Param("identifier") String identifier);

    @Transactional
    @Modifying
    @Query("update User u set u.password = ?2 where u.email = ?1")
    void updatePassword(String email, String password);
}