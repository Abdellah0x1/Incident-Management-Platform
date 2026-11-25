package com.incidentplatform.incident.dto.response;

import com.incidentplatform.incident.entity.IncidentSeverity;
import com.incidentplatform.incident.entity.IncidentStatus;

import java.time.LocalDateTime;

public record IncidentSummaryResponse(
        Long id,
        String title,
        IncidentSeverity severity,
        IncidentStatus status,
        UserSummary assignedTo,
        UserSummary createdBy,
        LocalDateTime createdAt
) {
}
