package com.incidentplatform.common.Exception;

import com.incidentplatform.incident.entity.IncidentStatus;

public class InvalidTransitionException extends RuntimeException {
    public InvalidTransitionException(IncidentStatus from, IncidentStatus to) {
        super(String.format("Invalid status transition from %s to %s", from, to));
    }
}
