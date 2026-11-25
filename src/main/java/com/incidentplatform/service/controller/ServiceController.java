package com.incidentplatform.service.controller;

import com.incidentplatform.common.dto.PageResponse;
import com.incidentplatform.service.dto.request.CreateServiceRequest;
import com.incidentplatform.service.dto.request.UpdateServiceRequest;
import com.incidentplatform.service.dto.request.UpdateServiceStatusRequest;
import com.incidentplatform.service.dto.response.ServiceResponse;
import com.incidentplatform.service.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping
    public ResponseEntity<ServiceResponse> createService(@Valid @RequestBody CreateServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceService.createService(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ServiceResponse>> getAllServices(
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        return ResponseEntity.ok(serviceService.getAllServices(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getService(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getService(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateService(
            @PathVariable Long id,
            @Valid @RequestBody UpdateServiceRequest request) {
        return ResponseEntity.ok(serviceService.updateService(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ServiceResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateServiceStatusRequest request) {
        return ResponseEntity.ok(serviceService.updateStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
