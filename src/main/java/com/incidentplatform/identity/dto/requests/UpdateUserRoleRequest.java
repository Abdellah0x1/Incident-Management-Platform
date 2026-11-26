package com.incidentplatform.identity.dto.requests;

import com.incidentplatform.identity.entity.ROLE;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(
        @NotNull(message = "Role is required")
        ROLE role
) {
}
