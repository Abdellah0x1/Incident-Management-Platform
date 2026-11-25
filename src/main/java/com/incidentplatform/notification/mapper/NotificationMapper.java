package com.incidentplatform.notification.mapper;

import com.incidentplatform.notification.dto.NotificationResponse;
import com.incidentplatform.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getIncident() != null ? notification.getIncident().getId() : null,
                notification.getCreatedAt()
        );
    }
}
