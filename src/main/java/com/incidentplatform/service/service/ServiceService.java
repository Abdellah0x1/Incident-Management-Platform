package com.incidentplatform.service.service;

import com.incidentplatform.common.Exception.ApiException;
import com.incidentplatform.common.Exception.ResourceNotFoundException;
import com.incidentplatform.common.dto.PageResponse;
import com.incidentplatform.service.dto.request.CreateServiceRequest;
import com.incidentplatform.service.dto.request.UpdateServiceRequest;
import com.incidentplatform.service.dto.response.ServiceResponse;
import com.incidentplatform.service.entity.ServiceStatus;
import com.incidentplatform.service.mapper.ServiceMapper;
import com.incidentplatform.service.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper mapper;

    @Transactional
    public ServiceResponse createService(CreateServiceRequest request) {
        if (serviceRepository.existsByTitleIgnoreCase(request.title())) {
            throw new ApiException("Service with title '" + request.title() + "' already exists");
        }

        com.incidentplatform.service.entity.Service service = new com.incidentplatform.service.entity.Service();
        service.setTitle(request.title());
        service.setDescription(request.description());
        service.setStatus(request.status() != null ? request.status() : ServiceStatus.OPERATIONAL);

        return mapper.toResponse(serviceRepository.save(service));
    }

    @Transactional(readOnly = true)
    public ServiceResponse getService(Long serviceId) {
        return mapper.toResponse(findServiceOrThrow(serviceId));
    }

    @Transactional(readOnly = true)
    public PageResponse<ServiceResponse> getAllServices(Pageable pageable) {
        return PageResponse.from(serviceRepository.findAll(pageable).map(mapper::toResponse));
    }

    @Transactional
    public ServiceResponse updateService(Long serviceId, UpdateServiceRequest request) {
        com.incidentplatform.service.entity.Service service = findServiceOrThrow(serviceId);

        if (request.title() != null && !request.title().isBlank()) {
            if (!request.title().equalsIgnoreCase(service.getTitle())
                    && serviceRepository.existsByTitleIgnoreCase(request.title())) {
                throw new ApiException("Service with title '" + request.title() + "' already exists");
            }
            service.setTitle(request.title());
        }
        if (request.description() != null && !request.description().isBlank()) {
            service.setDescription(request.description());
        }
        if (request.status() != null) {
            service.setStatus(request.status());
        }

        return mapper.toResponse(serviceRepository.save(service));
    }

    @Transactional
    public ServiceResponse updateStatus(Long serviceId, ServiceStatus status) {
        com.incidentplatform.service.entity.Service service = findServiceOrThrow(serviceId);
        service.setStatus(status);
        return mapper.toResponse(serviceRepository.save(service));
    }

    @Transactional
    public void deleteService(Long serviceId) {
        com.incidentplatform.service.entity.Service service = findServiceOrThrow(serviceId);
        serviceRepository.delete(service);
    }

    private com.incidentplatform.service.entity.Service findServiceOrThrow(Long serviceId) {
        return serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", serviceId));
    }
}
