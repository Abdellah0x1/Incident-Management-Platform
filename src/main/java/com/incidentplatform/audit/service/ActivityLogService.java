package com.incidentplatform.audit.service;

import com.incidentplatform.audit.dto.ActivityLogDTO;
import com.incidentplatform.audit.mapper.ActivityLogMapper;
import com.incidentplatform.audit.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper mapper;

    @Transactional(readOnly = true)
    public List<ActivityLogDTO> getAllLogs() {
        return activityLogRepository.findAllByOrderByTimestampDesc().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityLogDTO> getIncidentLogs(Long incidentId) {
        return activityLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc("Incident", incidentId).stream()
                .map(mapper::toDTO)
                .toList();
    }
}
