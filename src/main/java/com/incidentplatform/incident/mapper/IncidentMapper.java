package com.incidentplatform.incident.mapper;


import com.incidentplatform.incident.dto.IncidentDTO;
import com.incidentplatform.incident.entity.Incident;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {
    public IncidentDTO toDTO(Incident incident) {
        IncidentDTO incidentDTO = new IncidentDTO();

        incidentDTO.setTitle(incident.getTitle());
        incidentDTO.setDescription(incident.getDescription());
        incidentDTO.setStatus(incident.getStatus());
        incidentDTO.setSeverity(incident.getSeverity());
        incidentDTO.setCreatedAt(incident.getCreatedAt());
        incidentDTO.setUpdatedAt(incident.getUpdatedAt());
        
        return incidentDTO;
    }
}
