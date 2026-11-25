package com.incidentplatform.team.dto.response;

import java.time.LocalDateTime;

public record TeamSummaryResponse(
        Long id,
        String name,
        String description,
        int memberCount,
        LocalDateTime createdAt
) {
}
