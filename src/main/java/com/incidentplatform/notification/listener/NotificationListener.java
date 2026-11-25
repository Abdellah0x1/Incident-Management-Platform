package com.incidentplatform.notification.listener;

import com.incidentplatform.identity.entity.User;
import com.incidentplatform.incident.entity.Incident;
import com.incidentplatform.incident.event.IncidentAssignedEvent;
import com.incidentplatform.incident.event.IncidentCreatedEvent;
import com.incidentplatform.incident.event.IncidentStatusChangedEvent;
import com.incidentplatform.notification.entity.Notification;
import com.incidentplatform.notification.entity.NotificationType;
import com.incidentplatform.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final NotificationRepository notificationRepository;

    @EventListener
    public void onIncidentCreated(IncidentCreatedEvent event) {
        Incident incident = event.incident();
        User assignee = incident.getAssignedTo();
        if (assignee != null && !assignee.getId().equals(event.actor().getId())) {
            saveNotification(
                    assignee,
                    incident,
                    "New Incident Assigned",
                    String.format("Incident #%d '%s' (%s) was created and assigned to you.",
                            incident.getId(), incident.getTitle(), incident.getSeverity()),
                    NotificationType.INCIDENT_CREATED
            );
        }
    }

    @EventListener
    public void onIncidentStatusChanged(IncidentStatusChangedEvent event) {
        Incident incident = event.incident();
        Set<User> recipients = new HashSet<>();
        if (incident.getCreatedBy() != null) {
            recipients.add(incident.getCreatedBy());
        }
        if (incident.getAssignedTo() != null) {
            recipients.add(incident.getAssignedTo());
        }
        recipients.removeIf(u -> u.getId().equals(event.actor().getId()));

        for (User recipient : recipients) {
            saveNotification(
                    recipient,
                    incident,
                    "Incident Status Updated",
                    String.format("Incident #%d '%s' changed from %s to %s.",
                            incident.getId(), incident.getTitle(), event.oldStatus(), event.newStatus()),
                    NotificationType.STATUS_CHANGED
            );
        }
    }

    @EventListener
    public void onIncidentAssigned(IncidentAssignedEvent event) {
        User newAssignee = event.newAssignee();
        if (newAssignee != null && !newAssignee.getId().equals(event.actor().getId())) {
            Incident incident = event.incident();
            saveNotification(
                    newAssignee,
                    incident,
                    "Incident Assigned to You",
                    String.format("You have been assigned to incident #%d '%s'.",
                            incident.getId(), incident.getTitle()),
                    NotificationType.INCIDENT_ASSIGNED
            );
        }
    }

    private void saveNotification(User recipient, Incident incident, String title, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setIncident(incident);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notificationRepository.save(notification);
        log.debug("Created {} notification for user {}", type, recipient.getUsername());
    }
}
