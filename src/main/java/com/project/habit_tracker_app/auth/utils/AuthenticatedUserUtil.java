package com.project.habit_tracker_app.auth.utils;

import com.project.habit_tracker_app.auth.entities.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserUtil {

    public Long getCurrentUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
    }
}
