package com.incidentplatform.team.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTeamRequest(
        @NotBlank(message = "Team name is required")
        @Size(max = 100, message = "Team name must not exceed 100 characters")
        String name,

        String description
) {
}
