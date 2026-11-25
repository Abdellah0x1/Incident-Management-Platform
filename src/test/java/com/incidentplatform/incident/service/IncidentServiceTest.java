package com.incidentplatform.incident.service;

import com.incidentplatform.common.Exception.InvalidTransitionException;
import com.incidentplatform.common.Exception.ResourceNotFoundException;
import com.incidentplatform.common.dto.PageResponse;
import com.incidentplatform.common.utils.AuthUtils;
import com.incidentplatform.identity.entity.ROLE;
import com.incidentplatform.identity.entity.User;
import com.incidentplatform.identity.repository.UserRepository;
import com.incidentplatform.incident.dto.request.CreateIncidentRequest;
import com.incidentplatform.incident.dto.request.UpdateIncidentRequest;
import com.incidentplatform.incident.dto.response.IncidentResponse;
import com.incidentplatform.incident.dto.response.IncidentSummaryResponse;
import com.incidentplatform.incident.entity.Incident;
import com.incidentplatform.incident.entity.IncidentSeverity;
import com.incidentplatform.incident.entity.IncidentStatus;
import com.incidentplatform.incident.event.IncidentAssignedEvent;
import com.incidentplatform.incident.event.IncidentCreatedEvent;
import com.incidentplatform.incident.event.IncidentStatusChangedEvent;
import com.incidentplatform.incident.mapper.IncidentMapper;
import com.incidentplatform.incident.repository.IncidentRepository;
import com.incidentplatform.service.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class IncidentServiceTest {

    private Map<Long, Incident> incidentStore;
    private Map<Long, User> userStore;
    private AtomicLong idSeq;
    private List<Object> publishedEvents;
    private IncidentService incidentService;
    private User currentUser;

    @BeforeEach
    void setUp() {
        incidentStore = new HashMap<>();
        userStore = new HashMap<>();
        idSeq = new AtomicLong(1L);
        publishedEvents = new ArrayList<>();

        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("alice");
        currentUser.setEmail("alice@example.com");
        currentUser.setRole(ROLE.INCIDENT_MANAGER);
        userStore.put(1L, currentUser);

        IncidentRepository incidentRepository = (IncidentRepository) Proxy.newProxyInstance(
                IncidentRepository.class.getClassLoader(),
                new Class<?>[]{IncidentRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "save" -> {
                        Incident inc = (Incident) args[0];
                        if (inc.getId() == null) {
                            inc.setId(idSeq.getAndIncrement());
                        }
                        incidentStore.put(inc.getId(), inc);
                        yield inc;
                    }
                    case "findById" -> Optional.ofNullable(incidentStore.get(args[0]));
                    case "findAll" -> {
                        if (args != null && args.length == 2 && args[0] instanceof Specification<?> && args[1] instanceof Pageable pageable) {
                            List<Incident> all = incidentStore.values().stream().toList();
                            yield new PageImpl<>(all, pageable, all.size());
                        }
                        yield incidentStore.values().stream().toList();
                    }
                    case "delete" -> {
                        Incident inc = (Incident) args[0];
                        incidentStore.remove(inc.getId());
                        yield null;
                    }
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );

        UserRepository userRepository = (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class<?>[]{UserRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "findById" -> Optional.ofNullable(userStore.get(args[0]));
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );

        ServiceRepository serviceRepository = (ServiceRepository) Proxy.newProxyInstance(
                ServiceRepository.class.getClassLoader(),
                new Class<?>[]{ServiceRepository.class},
                (proxy, method, args) -> Optional.empty()
        );

        AuthUtils authUtils = new AuthUtils(userRepository) {
            @Override
            public User getCurrentUser() {
                return currentUser;
            }
        };

        incidentService = new IncidentService(
                incidentRepository,
                userRepository,
                serviceRepository,
                new IncidentMapper(),
                authUtils,
                publishedEvents::add
        );
    }

    @Test
    void createIncidentSetsCreatedByOpenStatusAndPublishesEvent() {
        CreateIncidentRequest req = new CreateIncidentRequest(
                "DB Outage", "Primary DB unreachable", IncidentSeverity.CRITICAL, null, null
        );

        IncidentResponse response = incidentService.createIncident(req);

        assertEquals(1L, response.id());
        assertEquals("DB Outage", response.title());
        assertEquals(IncidentStatus.OPEN, response.status());
        assertEquals(IncidentSeverity.CRITICAL, response.severity());
        assertEquals("alice", response.createdBy().username());
        assertEquals(1, publishedEvents.size());
        assertInstanceOf(IncidentCreatedEvent.class, publishedEvents.get(0));
    }

    @Test
    void transitionStatusValidUpdatesStatusTimestampAndPublishesEvent() {
        Incident incident = new Incident();
        incident.setId(1L);
        incident.setTitle("Test");
        incident.setSeverity(IncidentSeverity.HIGH);
        incident.setStatus(IncidentStatus.OPEN);
        incident.setCreatedBy(currentUser);
        incidentStore.put(1L, incident);

        IncidentResponse response = incidentService.transitionStatus(1L, IncidentStatus.ACKNOWLEDGED);

        assertEquals(IncidentStatus.ACKNOWLEDGED, response.status());
        assertNotNull(response.acknowledgedAt());
        assertEquals(1, publishedEvents.size());
        IncidentStatusChangedEvent event = assertInstanceOf(IncidentStatusChangedEvent.class, publishedEvents.get(0));
        assertEquals(IncidentStatus.OPEN, event.oldStatus());
        assertEquals(IncidentStatus.ACKNOWLEDGED, event.newStatus());
    }

    @Test
    void transitionStatusInvalidThrowsConflictException() {
        Incident incident = new Incident();
        incident.setId(1L);
        incident.setStatus(IncidentStatus.OPEN);
        incidentStore.put(1L, incident);

        assertThrows(InvalidTransitionException.class,
                () -> incidentService.transitionStatus(1L, IncidentStatus.RESOLVED));
        assertEquals(IncidentStatus.OPEN, incidentStore.get(1L).getStatus());
        assertTrue(publishedEvents.isEmpty());
    }

    @Test
    void updateIncidentModifiesProvidedFieldsOnly() {
        Incident incident = new Incident();
        incident.setId(1L);
        incident.setTitle("Old Title");
        incident.setDescription("Old Desc");
        incident.setSeverity(IncidentSeverity.LOW);
        incident.setStatus(IncidentStatus.OPEN);
        incident.setCreatedBy(currentUser);
        incidentStore.put(1L, incident);

        IncidentResponse response = incidentService.updateIncident(
                1L, new UpdateIncidentRequest("New Title", null, IncidentSeverity.HIGH, null)
        );

        assertEquals("New Title", response.title());
        assertEquals("Old Desc", response.description());
        assertEquals(IncidentSeverity.HIGH, response.severity());
    }

    @Test
    void getIncidentNotFoundThrowsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> incidentService.getIncident(99L));
    }

    @Test
    void assignIncidentSetsAssigneeAndPublishesEvent() {
        User assignee = new User();
        assignee.setId(2L);
        assignee.setUsername("bob");
        assignee.setEmail("bob@example.com");
        userStore.put(2L, assignee);

        Incident incident = new Incident();
        incident.setId(1L);
        incident.setStatus(IncidentStatus.OPEN);
        incident.setSeverity(IncidentSeverity.MEDIUM);
        incident.setCreatedBy(currentUser);
        incidentStore.put(1L, incident);

        IncidentResponse response = incidentService.assignIncident(1L, 2L);

        assertNotNull(response.assignedTo());
        assertEquals("bob", response.assignedTo().username());
        assertEquals(1, publishedEvents.size());
        assertInstanceOf(IncidentAssignedEvent.class, publishedEvents.get(0));
    }

    @Test
    void searchIncidentsReturnsPagedResults() {
        Incident incident = new Incident();
        incident.setId(1L);
        incident.setTitle("DB Latency");
        incident.setSeverity(IncidentSeverity.HIGH);
        incident.setStatus(IncidentStatus.OPEN);
        incident.setCreatedBy(currentUser);
        incidentStore.put(1L, incident);

        PageResponse<IncidentSummaryResponse> page = incidentService.searchIncidents(
                IncidentStatus.OPEN, IncidentSeverity.HIGH, null, null, null, null, "db", PageRequest.of(0, 10)
        );

        assertEquals(1, page.totalElements());
        assertEquals("DB Latency", page.content().get(0).title());
        assertTrue(page.last());
    }
}
