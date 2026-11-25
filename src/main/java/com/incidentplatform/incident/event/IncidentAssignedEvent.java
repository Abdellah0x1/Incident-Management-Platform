package com.incidentplatform.incident.event;

import com.incidentplatform.identity.entity.User;
import com.incidentplatform.incident.entity.Incident;

public record IncidentAssignedEvent(
        Incident incident,
        User previousAssignee,
        User newAssignee,
        User actor
) {
}
