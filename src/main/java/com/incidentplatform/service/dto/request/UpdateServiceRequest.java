package com.incidentplatform.service.dto.request;

import com.incidentplatform.service.entity.ServiceStatus;
import jakarta.validation.constraints.Size;

public record UpdateServiceRequest(
        @Size(max = 120, message = "Title must not exceed 120 characters")
        String title,

        String description,
        ServiceStatus status
) {
}
