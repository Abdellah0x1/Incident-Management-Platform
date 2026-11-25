package com.incidentplatform.audit.controller;

import com.incidentplatform.audit.dto.ActivityLogDTO;
import com.incidentplatform.audit.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<List<ActivityLogDTO>> getAllLogs() {
        return ResponseEntity.ok(activityLogService.getAllLogs());
    }

    @GetMapping("/incidents/{incidentId}")
    public ResponseEntity<List<ActivityLogDTO>> getIncidentLogs(@PathVariable Long incidentId) {
        return ResponseEntity.ok(activityLogService.getIncidentLogs(incidentId));
    }
}
