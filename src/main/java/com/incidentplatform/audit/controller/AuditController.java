package com.incidentplatform.audit.controller;

import com.incidentplatform.audit.dto.ActivityLogDTO;
import com.incidentplatform.audit.service.ActivityLogService;
import com.incidentplatform.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<PageResponse<ActivityLogDTO>> getAllLogs(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(activityLogService.getAllLogs(pageable));
    }

    @GetMapping("/incidents/{incidentId}")
    public ResponseEntity<PageResponse<ActivityLogDTO>> getIncidentLogs(
            @PathVariable Long incidentId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(activityLogService.getIncidentLogs(incidentId, pageable));
    }
}
