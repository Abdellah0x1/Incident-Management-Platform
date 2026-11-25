package com.incidentplatform.notification.dto;

import com.incidentplatform.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String title,
        String message,
        NotificationType type,
        boolean isRead,
        Long incidentId,
        LocalDateTime createdAt
) {
}
