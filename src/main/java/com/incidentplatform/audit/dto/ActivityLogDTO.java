package com.incidentplatform.audit.dto;


import java.time.LocalDateTime;

public record ActivityLogDTO(
        String id,
        String actorId,
        String incidentId,
        LocalDateTime timestamp
){
}
