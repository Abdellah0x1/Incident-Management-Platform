package com.incidentplatform.incident.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IncidentStatusTest {

    @Test
    void openCanTransitionToAcknowledgedAndClosed() {
        assertTrue(IncidentStatus.OPEN.canTransitionTo(IncidentStatus.ACKNOWLEDGED));
        assertTrue(IncidentStatus.OPEN.canTransitionTo(IncidentStatus.CLOSED));
        assertFalse(IncidentStatus.OPEN.canTransitionTo(IncidentStatus.INVESTIGATING));
        assertFalse(IncidentStatus.OPEN.canTransitionTo(IncidentStatus.RESOLVED));
        assertFalse(IncidentStatus.OPEN.canTransitionTo(IncidentStatus.OPEN));
    }

    @Test
    void acknowledgedCanTransitionToInvestigatingAndClosed() {
        assertTrue(IncidentStatus.ACKNOWLEDGED.canTransitionTo(IncidentStatus.INVESTIGATING));
        assertTrue(IncidentStatus.ACKNOWLEDGED.canTransitionTo(IncidentStatus.CLOSED));
        assertFalse(IncidentStatus.ACKNOWLEDGED.canTransitionTo(IncidentStatus.OPEN));
        assertFalse(IncidentStatus.ACKNOWLEDGED.canTransitionTo(IncidentStatus.RESOLVED));
    }

    @Test
    void investigatingCanTransitionToResolvedAndClosed() {
        assertTrue(IncidentStatus.INVESTIGATING.canTransitionTo(IncidentStatus.RESOLVED));
        assertTrue(IncidentStatus.INVESTIGATING.canTransitionTo(IncidentStatus.CLOSED));
        assertFalse(IncidentStatus.INVESTIGATING.canTransitionTo(IncidentStatus.OPEN));
        assertFalse(IncidentStatus.INVESTIGATING.canTransitionTo(IncidentStatus.ACKNOWLEDGED));
    }

    @Test
    void resolvedCanTransitionToClosedOrReopen() {
        assertTrue(IncidentStatus.RESOLVED.canTransitionTo(IncidentStatus.CLOSED));
        assertTrue(IncidentStatus.RESOLVED.canTransitionTo(IncidentStatus.OPEN));
        assertFalse(IncidentStatus.RESOLVED.canTransitionTo(IncidentStatus.ACKNOWLEDGED));
        assertFalse(IncidentStatus.RESOLVED.canTransitionTo(IncidentStatus.INVESTIGATING));
    }

    @Test
    void closedIsTerminal() {
        for (IncidentStatus status : IncidentStatus.values()) {
            assertFalse(IncidentStatus.CLOSED.canTransitionTo(status));
        }
    }

    @Test
    void nullTargetReturnsFalse() {
        assertFalse(IncidentStatus.OPEN.canTransitionTo(null));
    }
}
