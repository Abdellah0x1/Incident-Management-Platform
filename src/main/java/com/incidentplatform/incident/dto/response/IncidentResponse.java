package com.incidentplatform.incident.dto.response;

import com.incidentplatform.incident.entity.IncidentSeverity;
import com.incidentplatform.incident.entity.IncidentStatus;

import java.time.LocalDateTime;

public record IncidentResponse(
        Long id,
        String title,
        String description,
        IncidentSeverity severity,
        IncidentStatus status,
        UserSummary assignedTo,
        UserSummary createdBy,
        Long serviceId,
        String serviceTitle,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime acknowledgedAt,
        LocalDateTime resolvedAt,
        LocalDateTime closedAt,
        int commentCount,
        int attachmentCount
) {
}
