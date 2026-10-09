package com.incidentplatform.incident.dto;

import com.incidentplatform.incident.entity.IncidentSeverity;
import com.incidentplatform.incident.entity.IncidentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentDTO {

    private String title;

    private String description;

    private IncidentStatus status;

    private IncidentSeverity severity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
