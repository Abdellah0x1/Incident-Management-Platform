package com.incidentplatform.audit.mapper;

import com.incidentplatform.audit.dto.ActivityLogDTO;
import com.incidentplatform.audit.entity.ActivityLog;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMapper {

    public ActivityLogDTO toDTO(ActivityLog log) {
        return new ActivityLogDTO(
                log.getId(),
                log.getAction(),
                log.getEntityType(),
                log.getEntityId(),
                log.getOldValue(),
                log.getNewValue(),
                log.getActor() != null ? log.getActor().getId() : null,
                log.getActor() != null ? log.getActor().getUsername() : null,
                log.getTimestamp()
        );
    }
}
