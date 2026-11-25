package com.incidentplatform.service.dto.request;

import com.incidentplatform.service.entity.ServiceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateServiceRequest(
        @NotBlank(message = "Service title is required")
        @Size(max = 120, message = "Title must not exceed 120 characters")
        String title,

        @NotBlank(message = "Service description is required")
        String description,

        ServiceStatus status
) {
}
