package com.incidentplatform.service.mapper;

import com.incidentplatform.service.dto.response.ServiceResponse;
import com.incidentplatform.service.entity.Service;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    public ServiceResponse toResponse(Service service) {
        return new ServiceResponse(
                service.getId(),
                service.getTitle(),
                service.getDescription(),
                service.getStatus(),
                service.getCreatedAt()
        );
    }
}
