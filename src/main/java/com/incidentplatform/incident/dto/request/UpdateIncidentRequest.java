package com.incidentplatform.incident.dto.request;

import com.incidentplatform.incident.entity.IncidentSeverity;
import jakarta.validation.constraints.Size;

public record UpdateIncidentRequest(
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        String description,
        IncidentSeverity severity,
        Long serviceId
) {
}
