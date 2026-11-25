package com.incidentplatform.team.dto.response;

import com.incidentplatform.incident.dto.response.UserSummary;

import java.time.LocalDateTime;
import java.util.List;

public record TeamResponse(
        Long id,
        String name,
        String description,
        List<UserSummary> members,
        LocalDateTime createdAt
) {
}
