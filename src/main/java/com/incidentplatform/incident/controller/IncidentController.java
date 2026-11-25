package com.incidentplatform.incident.controller;

import com.incidentplatform.incident.dto.request.AssignIncidentRequest;
import com.incidentplatform.incident.dto.request.CreateIncidentRequest;
import com.incidentplatform.incident.dto.request.TransitionStatusRequest;
import com.incidentplatform.incident.dto.request.UpdateIncidentRequest;
import com.incidentplatform.incident.dto.response.IncidentResponse;
import com.incidentplatform.incident.dto.response.IncidentSummaryResponse;
import com.incidentplatform.incident.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping
    public ResponseEntity<IncidentResponse> createIncident(@Valid @RequestBody CreateIncidentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(incidentService.createIncident(request));
    }

    @GetMapping
    public ResponseEntity<List<IncidentSummaryResponse>> getAllIncidents() {
        return ResponseEntity.ok(incidentService.getAllIncidents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> getIncident(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getIncident(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidentResponse> updateIncident(
            @PathVariable Long id,
            @Valid @RequestBody UpdateIncidentRequest request) {
        return ResponseEntity.ok(incidentService.updateIncident(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<IncidentResponse> transitionStatus(
            @PathVariable Long id,
            @Valid @RequestBody TransitionStatusRequest request) {
        return ResponseEntity.ok(incidentService.transitionStatus(id, request.status()));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<IncidentResponse> assignIncident(
            @PathVariable Long id,
            @RequestBody AssignIncidentRequest request) {
        return ResponseEntity.ok(incidentService.assignIncident(id, request.assignedToId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }
}
