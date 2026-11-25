package com.incidentplatform.notification.listener;

import com.incidentplatform.identity.entity.User;
import com.incidentplatform.incident.entity.Incident;
import com.incidentplatform.incident.entity.IncidentSeverity;
import com.incidentplatform.incident.entity.IncidentStatus;
import com.incidentplatform.incident.event.IncidentAssignedEvent;
import com.incidentplatform.incident.event.IncidentCreatedEvent;
import com.incidentplatform.incident.event.IncidentStatusChangedEvent;
import com.incidentplatform.notification.entity.Notification;
import com.incidentplatform.notification.entity.NotificationType;
import com.incidentplatform.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationListenerTest {

    private List<Notification> savedNotifications;
    private NotificationListener listener;
    private User actor;
    private User assignee;

    @BeforeEach
    void setUp() {
        savedNotifications = new ArrayList<>();
        NotificationRepository repo = (NotificationRepository) Proxy.newProxyInstance(
                NotificationRepository.class.getClassLoader(),
                new Class<?>[]{NotificationRepository.class},
                (proxy, method, args) -> {
                    if ("save".equals(method.getName())) {
                        Notification n = (Notification) args[0];
                        savedNotifications.add(n);
                        return n;
                    }
                    throw new UnsupportedOperationException(method.getName());
                }
        );

        listener = new NotificationListener(repo);

        actor = new User();
        actor.setId(1L);
        actor.setUsername("alice");

        assignee = new User();
        assignee.setId(2L);
        assignee.setUsername("bob");
    }

    @Test
    void onIncidentCreatedNotifiesAssigneeWhenDifferentFromActor() {
        Incident incident = new Incident();
        incident.setId(10L);
        incident.setTitle("API Down");
        incident.setSeverity(IncidentSeverity.CRITICAL);
        incident.setCreatedBy(actor);
        incident.setAssignedTo(assignee);

        listener.onIncidentCreated(new IncidentCreatedEvent(incident, actor));

        assertEquals(1, savedNotifications.size());
        Notification n = savedNotifications.get(0);
        assertEquals(assignee, n.getUser());
        assertEquals(NotificationType.INCIDENT_CREATED, n.getType());
        assertFalse(n.isRead());
    }

    @Test
    void onIncidentStatusChangedNotifiesCreatorAndAssigneeExcludingActor() {
        Incident incident = new Incident();
        incident.setId(10L);
        incident.setTitle("API Down");
        incident.setCreatedBy(actor);
        incident.setAssignedTo(assignee);

        listener.onIncidentStatusChanged(
                new IncidentStatusChangedEvent(incident, IncidentStatus.OPEN, IncidentStatus.ACKNOWLEDGED, actor)
        );

        assertEquals(1, savedNotifications.size());
        assertEquals(assignee, savedNotifications.get(0).getUser());
        assertEquals(NotificationType.STATUS_CHANGED, savedNotifications.get(0).getType());
    }

    @Test
    void onIncidentAssignedNotifiesNewAssignee() {
        Incident incident = new Incident();
        incident.setId(10L);
        incident.setTitle("API Down");

        listener.onIncidentAssigned(new IncidentAssignedEvent(incident, null, assignee, actor));

        assertEquals(1, savedNotifications.size());
        assertEquals(assignee, savedNotifications.get(0).getUser());
        assertEquals(NotificationType.INCIDENT_ASSIGNED, savedNotifications.get(0).getType());
    }
}
