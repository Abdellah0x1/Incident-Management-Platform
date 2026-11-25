package com.incidentplatform.incident.service;

import com.incidentplatform.common.Exception.InvalidTransitionException;
import com.incidentplatform.common.Exception.ResourceNotFoundException;
import com.incidentplatform.common.utils.AuthUtils;
import com.incidentplatform.identity.entity.User;
import com.incidentplatform.identity.repository.UserRepository;
import com.incidentplatform.incident.dto.request.CreateIncidentRequest;
import com.incidentplatform.incident.dto.request.UpdateIncidentRequest;
import com.incidentplatform.incident.dto.response.IncidentResponse;
import com.incidentplatform.incident.dto.response.IncidentSummaryResponse;
import com.incidentplatform.incident.entity.Incident;
import com.incidentplatform.incident.entity.IncidentStatus;
import com.incidentplatform.incident.event.IncidentAssignedEvent;
import com.incidentplatform.incident.event.IncidentCreatedEvent;
import com.incidentplatform.incident.event.IncidentDeletedEvent;
import com.incidentplatform.incident.event.IncidentStatusChangedEvent;
import com.incidentplatform.incident.event.IncidentUpdatedEvent;
import com.incidentplatform.incident.mapper.IncidentMapper;
import com.incidentplatform.incident.repository.IncidentRepository;
import com.incidentplatform.service.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final IncidentMapper mapper;
    private final AuthUtils authUtils;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public IncidentResponse createIncident(CreateIncidentRequest request) {
        User currentUser = authUtils.getCurrentUser();

        Incident incident = new Incident();
        incident.setTitle(request.title());
        incident.setDescription(request.description());
        incident.setSeverity(request.severity());
        incident.setStatus(IncidentStatus.OPEN);
        incident.setCreatedBy(currentUser);

        if (request.assignedToId() != null) {
            incident.setAssignedTo(findUserOrThrow(request.assignedToId()));
        }
        if (request.serviceId() != null) {
            incident.setService(findServiceOrThrow(request.serviceId()));
        }

        Incident saved = incidentRepository.save(incident);
        eventPublisher.publishEvent(new IncidentCreatedEvent(saved, currentUser));
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public IncidentResponse getIncident(Long incidentId) {
        Incident incident = findIncidentOrThrow(incidentId);
        return mapper.toResponse(incident);
    }

    @Transactional(readOnly = true)
    public List<IncidentSummaryResponse> getAllIncidents() {
        return incidentRepository.findAll().stream()
                .map(mapper::toSummary)
                .toList();
    }

    @Transactional
    public IncidentResponse updateIncident(Long incidentId, UpdateIncidentRequest request) {
        Incident incident = findIncidentOrThrow(incidentId);

        if (request.title() != null && !request.title().isBlank()) {
            incident.setTitle(request.title());
        }
        if (request.description() != null) {
            incident.setDescription(request.description());
        }
        if (request.severity() != null) {
            incident.setSeverity(request.severity());
        }
        if (request.serviceId() != null) {
            incident.setService(findServiceOrThrow(request.serviceId()));
        }

        Incident saved = incidentRepository.save(incident);
        eventPublisher.publishEvent(new IncidentUpdatedEvent(saved, authUtils.getCurrentUser(), saved.getTitle()));
        return mapper.toResponse(saved);
    }

    @Transactional
    public IncidentResponse transitionStatus(Long incidentId, IncidentStatus targetStatus) {
        Incident incident = findIncidentOrThrow(incidentId);
        IncidentStatus currentStatus = incident.getStatus();

        if (!currentStatus.canTransitionTo(targetStatus)) {
            throw new InvalidTransitionException(currentStatus, targetStatus);
        }

        incident.setStatus(targetStatus);
        recordTransitionTimestamp(incident, targetStatus);

        Incident saved = incidentRepository.save(incident);
        eventPublisher.publishEvent(new IncidentStatusChangedEvent(saved, currentStatus, targetStatus, authUtils.getCurrentUser()));
        return mapper.toResponse(saved);
    }

    @Transactional
    public IncidentResponse assignIncident(Long incidentId, Long assignedToId) {
        Incident incident = findIncidentOrThrow(incidentId);
        User previousAssignee = incident.getAssignedTo();
        User newAssignee = assignedToId != null ? findUserOrThrow(assignedToId) : null;
        incident.setAssignedTo(newAssignee);

        Incident saved = incidentRepository.save(incident);
        eventPublisher.publishEvent(new IncidentAssignedEvent(saved, previousAssignee, newAssignee, authUtils.getCurrentUser()));
        return mapper.toResponse(saved);
    }

    @Transactional
    public void deleteIncident(Long incidentId) {
        Incident incident = findIncidentOrThrow(incidentId);
        String title = incident.getTitle();
        incidentRepository.delete(incident);
        eventPublisher.publishEvent(new IncidentDeletedEvent(incidentId, title, authUtils.getCurrentUser()));
    }

    private void recordTransitionTimestamp(Incident incident, IncidentStatus targetStatus) {
        LocalDateTime now = LocalDateTime.now();
        switch (targetStatus) {
            case ACKNOWLEDGED -> incident.setAcknowledgedAt(now);
            case RESOLVED -> incident.setResolvedAt(now);
            case CLOSED -> incident.setClosedAt(now);
            case OPEN -> {
                incident.setResolvedAt(null);
                incident.setClosedAt(null);
            }
            case INVESTIGATING -> {
                if (incident.getAcknowledgedAt() == null) {
                    incident.setAcknowledgedAt(now);
                }
            }
        }
    }

    private Incident findIncidentOrThrow(Long incidentId) {
        return incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident", "id", incidentId));
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private com.incidentplatform.service.entity.Service findServiceOrThrow(Long serviceId) {
        return serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", serviceId));
    }
}
