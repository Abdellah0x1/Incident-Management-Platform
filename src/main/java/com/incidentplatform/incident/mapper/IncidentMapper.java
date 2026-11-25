package com.incidentplatform.incident.mapper;

import com.incidentplatform.identity.entity.User;
import com.incidentplatform.incident.dto.response.IncidentResponse;
import com.incidentplatform.incident.dto.response.IncidentSummaryResponse;
import com.incidentplatform.incident.dto.response.UserSummary;
import com.incidentplatform.incident.entity.Incident;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {

    public IncidentResponse toResponse(Incident incident) {
        return new IncidentResponse(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getSeverity(),
                incident.getStatus(),
                toUserSummary(incident.getAssignedTo()),
                toUserSummary(incident.getCreatedBy()),
                incident.getService() != null ? incident.getService().getId() : null,
                incident.getService() != null ? incident.getService().getTitle() : null,
                incident.getCreatedAt(),
                incident.getUpdatedAt(),
                incident.getAcknowledgedAt(),
                incident.getResolvedAt(),
                incident.getClosedAt(),
                incident.getComments() != null ? incident.getComments().size() : 0,
                incident.getAttachments() != null ? incident.getAttachments().size() : 0
        );
    }

    public IncidentSummaryResponse toSummary(Incident incident) {
        return new IncidentSummaryResponse(
                incident.getId(),
                incident.getTitle(),
                incident.getSeverity(),
                incident.getStatus(),
                toUserSummary(incident.getAssignedTo()),
                toUserSummary(incident.getCreatedBy()),
                incident.getCreatedAt()
        );
    }

    private UserSummary toUserSummary(User user) {
        if (user == null) {
            return null;
        }
        return new UserSummary(user.getId(), user.getUsername(), user.getEmail());
    }
}
