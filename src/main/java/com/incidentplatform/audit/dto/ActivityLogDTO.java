package com.incidentplatform.audit.dto;

import java.time.LocalDateTime;

public record ActivityLogDTO(
        Long id,
        String action,
        String entityType,
        Long entityId,
        String oldValue,
        String newValue,
        Long actorId,
        String actorUsername,
        LocalDateTime timestamp
) {
}
