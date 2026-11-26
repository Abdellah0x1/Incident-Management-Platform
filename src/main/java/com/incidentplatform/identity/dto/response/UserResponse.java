package com.incidentplatform.identity.dto.response;

import com.incidentplatform.identity.entity.ROLE;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        ROLE role,
        LocalDateTime createdAt
) {
}
