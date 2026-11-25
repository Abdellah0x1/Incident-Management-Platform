package com.incidentplatform.service.dto.request;

import com.incidentplatform.service.entity.ServiceStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateServiceStatusRequest(
        @NotNull(message = "Service status is required")
        ServiceStatus status
) {
}
