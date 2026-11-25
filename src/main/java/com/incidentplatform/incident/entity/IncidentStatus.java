package com.incidentplatform.incident.entity;

import java.util.Map;
import java.util.Set;

public enum IncidentStatus {
    OPEN,
    ACKNOWLEDGED,
    INVESTIGATING,
    RESOLVED,
    CLOSED;

    private static final Map<IncidentStatus, Set<IncidentStatus>> TRANSITIONS = Map.of(
            OPEN, Set.of(ACKNOWLEDGED, CLOSED),
            ACKNOWLEDGED, Set.of(INVESTIGATING, CLOSED),
            INVESTIGATING, Set.of(RESOLVED, CLOSED),
            RESOLVED, Set.of(CLOSED, OPEN),
            CLOSED, Set.of()
    );

    public boolean canTransitionTo(IncidentStatus target) {
        if (target == null) {
            return false;
        }
        return TRANSITIONS.getOrDefault(this, Set.of()).contains(target);
    }
}
