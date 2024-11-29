package com.project.habit_tracker_app.dto;

import lombok.Builder;

@Builder
public record MailBody(String to, String subject, String text) {
}
