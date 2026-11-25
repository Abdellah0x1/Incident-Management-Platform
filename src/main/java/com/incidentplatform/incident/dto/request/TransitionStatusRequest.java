package com.incidentplatform.incident.dto.request;

import com.incidentplatform.incident.entity.IncidentStatus;
import jakarta.validation.constraints.NotNull;

public record TransitionStatusRequest(
        @NotNull(message = "Target status is required")
        IncidentStatus status
) {
}
