package com.incidentplatform.service.service;

import com.incidentplatform.common.Exception.ApiException;
import com.incidentplatform.common.Exception.ResourceNotFoundException;
import com.incidentplatform.service.dto.request.CreateServiceRequest;
import com.incidentplatform.service.dto.request.UpdateServiceRequest;
import com.incidentplatform.service.dto.response.ServiceResponse;
import com.incidentplatform.service.entity.Service;
import com.incidentplatform.service.entity.ServiceStatus;
import com.incidentplatform.service.mapper.ServiceMapper;
import com.incidentplatform.service.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class ServiceServiceTest {

    private Map<Long, Service> serviceStore;
    private AtomicLong idSeq;
    private ServiceService serviceService;

    @BeforeEach
    void setUp() {
        serviceStore = new HashMap<>();
        idSeq = new AtomicLong(1L);

        ServiceRepository repo = (ServiceRepository) Proxy.newProxyInstance(
                ServiceRepository.class.getClassLoader(),
                new Class<?>[]{ServiceRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "save" -> {
                        Service s = (Service) args[0];
                        if (s.getId() == null) {
                            s.setId(idSeq.getAndIncrement());
                        }
                        serviceStore.put(s.getId(), s);
                        yield s;
                    }
                    case "findById" -> Optional.ofNullable(serviceStore.get(args[0]));
                    case "findAll" -> serviceStore.values().stream().toList();
                    case "existsByTitleIgnoreCase" -> serviceStore.values().stream()
                            .anyMatch(s -> s.getTitle().equalsIgnoreCase((String) args[0]));
                    case "delete" -> {
                        Service s = (Service) args[0];
                        serviceStore.remove(s.getId());
                        yield null;
                    }
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );

        serviceService = new ServiceService(repo, new ServiceMapper());
    }

    @Test
    void createServiceDefaultsStatusToOperationalAndRejectsDuplicates() {
        ServiceResponse created = serviceService.createService(
                new CreateServiceRequest("Auth API", "Handles authentication", null)
        );

        assertEquals(ServiceStatus.OPERATIONAL, created.status());
        assertThrows(ApiException.class, () -> serviceService.createService(
                new CreateServiceRequest("auth api", "Duplicate", ServiceStatus.DEGRADED)
        ));
    }

    @Test
    void updateStatusAndFields() {
        ServiceResponse created = serviceService.createService(
                new CreateServiceRequest("Billing", "Stripe integration", ServiceStatus.OPERATIONAL)
        );

        ServiceResponse degraded = serviceService.updateStatus(created.id(), ServiceStatus.DEGRADED);
        assertEquals(ServiceStatus.DEGRADED, degraded.status());

        ServiceResponse updated = serviceService.updateService(
                created.id(), new UpdateServiceRequest("Billing v2", null, ServiceStatus.DOWN)
        );
        assertEquals("Billing v2", updated.title());
        assertEquals(ServiceStatus.DOWN, updated.status());
    }

    @Test
    void getServiceNotFoundThrowsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> serviceService.getService(999L));
    }
}
