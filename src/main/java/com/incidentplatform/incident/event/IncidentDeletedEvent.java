package com.incidentplatform.incident.event;

import com.incidentplatform.identity.entity.User;

public record IncidentDeletedEvent(
        Long incidentId,
        String title,
        User actor
) {
}
