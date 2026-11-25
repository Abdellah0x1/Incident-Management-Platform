package com.incidentplatform.incident.event;

import com.incidentplatform.identity.entity.User;
import com.incidentplatform.incident.entity.Incident;
import com.incidentplatform.incident.entity.IncidentStatus;

public record IncidentStatusChangedEvent(
        Incident incident,
        IncidentStatus oldStatus,
        IncidentStatus newStatus,
        User actor
) {
}
