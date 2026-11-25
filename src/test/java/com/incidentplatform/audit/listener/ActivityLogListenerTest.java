package com.incidentplatform.audit.listener;

import com.incidentplatform.audit.entity.ActivityLog;
import com.incidentplatform.audit.repository.ActivityLogRepository;
import com.incidentplatform.identity.entity.User;
import com.incidentplatform.incident.entity.Incident;
import com.incidentplatform.incident.entity.IncidentStatus;
import com.incidentplatform.incident.event.IncidentAssignedEvent;
import com.incidentplatform.incident.event.IncidentCreatedEvent;
import com.incidentplatform.incident.event.IncidentDeletedEvent;
import com.incidentplatform.incident.event.IncidentStatusChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActivityLogListenerTest {

    private List<ActivityLog> savedLogs;
    private ActivityLogListener listener;
    private User actor;

    @BeforeEach
    void setUp() {
        savedLogs = new ArrayList<>();
        ActivityLogRepository repo = (ActivityLogRepository) Proxy.newProxyInstance(
                ActivityLogRepository.class.getClassLoader(),
                new Class<?>[]{ActivityLogRepository.class},
                (proxy, method, args) -> {
                    if ("save".equals(method.getName())) {
                        ActivityLog log = (ActivityLog) args[0];
                        savedLogs.add(log);
                        return log;
                    }
                    throw new UnsupportedOperationException(method.getName());
                }
        );

        listener = new ActivityLogListener(repo);
        actor = new User();
        actor.setId(1L);
        actor.setUsername("alice");
    }

    @Test
    void logsCreatedStatusChangedAssignedAndDeletedEvents() {
        Incident incident = new Incident();
        incident.setId(5L);
        incident.setTitle("Cache Miss Storm");

        User bob = new User();
        bob.setId(2L);
        bob.setUsername("bob");

        listener.onIncidentCreated(new IncidentCreatedEvent(incident, actor));
        listener.onIncidentStatusChanged(
                new IncidentStatusChangedEvent(incident, IncidentStatus.OPEN, IncidentStatus.ACKNOWLEDGED, actor)
        );
        listener.onIncidentAssigned(new IncidentAssignedEvent(incident, null, bob, actor));
        listener.onIncidentDeleted(new IncidentDeletedEvent(5L, "Cache Miss Storm", actor));

        assertEquals(4, savedLogs.size());
        assertEquals("CREATED", savedLogs.get(0).getAction());
        assertEquals("STATUS_CHANGED", savedLogs.get(1).getAction());
        assertEquals("OPEN", savedLogs.get(1).getOldValue());
        assertEquals("ACKNOWLEDGED", savedLogs.get(1).getNewValue());
        assertEquals("ASSIGNED", savedLogs.get(2).getAction());
        assertEquals("unassigned", savedLogs.get(2).getOldValue());
        assertEquals("bob", savedLogs.get(2).getNewValue());
        assertEquals("DELETED", savedLogs.get(3).getAction());
    }
}
