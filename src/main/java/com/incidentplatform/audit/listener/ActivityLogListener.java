package com.incidentplatform.audit.listener;

import com.incidentplatform.audit.entity.ActivityLog;
import com.incidentplatform.audit.repository.ActivityLogRepository;
import com.incidentplatform.identity.entity.User;
import com.incidentplatform.incident.event.IncidentAssignedEvent;
import com.incidentplatform.incident.event.IncidentCreatedEvent;
import com.incidentplatform.incident.event.IncidentDeletedEvent;
import com.incidentplatform.incident.event.IncidentStatusChangedEvent;
import com.incidentplatform.incident.event.IncidentUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityLogListener {

    private static final String ENTITY_INCIDENT = "Incident";

    private final ActivityLogRepository activityLogRepository;

    @EventListener
    public void onIncidentCreated(IncidentCreatedEvent event) {
        saveLog(event.actor(), "CREATED", event.incident().getId(), null, event.incident().getTitle());
    }

    @EventListener
    public void onIncidentUpdated(IncidentUpdatedEvent event) {
        saveLog(event.actor(), "UPDATED", event.incident().getId(), null, event.summary());
    }

    @EventListener
    public void onIncidentStatusChanged(IncidentStatusChangedEvent event) {
        saveLog(
                event.actor(),
                "STATUS_CHANGED",
                event.incident().getId(),
                event.oldStatus().name(),
                event.newStatus().name()
        );
    }

    @EventListener
    public void onIncidentAssigned(IncidentAssignedEvent event) {
        String prev = event.previousAssignee() != null ? event.previousAssignee().getUsername() : "unassigned";
        String next = event.newAssignee() != null ? event.newAssignee().getUsername() : "unassigned";
        saveLog(event.actor(), "ASSIGNED", event.incident().getId(), prev, next);
    }

    @EventListener
    public void onIncidentDeleted(IncidentDeletedEvent event) {
        saveLog(event.actor(), "DELETED", event.incidentId(), event.title(), null);
    }

    private void saveLog(User actor, String action, Long entityId, String oldValue, String newValue) {
        ActivityLog entry = new ActivityLog();
        entry.setActor(actor);
        entry.setAction(action);
        entry.setEntityType(ENTITY_INCIDENT);
        entry.setEntityId(entityId);
        entry.setOldValue(oldValue);
        entry.setNewValue(newValue);
        activityLogRepository.save(entry);
        log.debug("Audit logged: {} on {} #{}", action, ENTITY_INCIDENT, entityId);
    }
}
