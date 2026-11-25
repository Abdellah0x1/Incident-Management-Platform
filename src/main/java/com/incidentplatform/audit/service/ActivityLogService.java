package com.incidentplatform.audit.service;

import com.incidentplatform.audit.dto.ActivityLogDTO;
import com.incidentplatform.audit.mapper.ActivityLogMapper;
import com.incidentplatform.audit.repository.ActivityLogRepository;
import com.incidentplatform.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper mapper;

    @Transactional(readOnly = true)
    public PageResponse<ActivityLogDTO> getAllLogs(Pageable pageable) {
        return PageResponse.from(
                activityLogRepository.findAllByOrderByTimestampDesc(pageable).map(mapper::toDTO)
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<ActivityLogDTO> getIncidentLogs(Long incidentId, Pageable pageable) {
        return PageResponse.from(
                activityLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc("Incident", incidentId, pageable)
                        .map(mapper::toDTO)
        );
    }
}
