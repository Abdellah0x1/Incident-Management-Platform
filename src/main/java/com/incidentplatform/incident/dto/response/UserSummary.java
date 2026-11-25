package com.incidentplatform.incident.dto.response;

public record UserSummary(
        Long id,
        String username,
        String email
) {
}
