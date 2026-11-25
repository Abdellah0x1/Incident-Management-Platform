package com.incidentplatform.service.dto.response;

import com.incidentplatform.service.entity.ServiceStatus;

import java.time.LocalDateTime;

public record ServiceResponse(
        Long id,
        String title,
        String description,
        ServiceStatus status,
        LocalDateTime createdAt
) {
}
