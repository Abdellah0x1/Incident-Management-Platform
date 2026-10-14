package com.incidentplatform.incident.service;


import com.incidentplatform.common.Exception.ResourceNotFoundException;
import com.incidentplatform.incident.dto.IncidentDTO;
import com.incidentplatform.incident.entity.Incident;
import com.incidentplatform.incident.mapper.IncidentMapper;
import com.incidentplatform.incident.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentService {
    @Autowired
    private IncidentMapper mapper;

    @Autowired
    private IncidentRepository incidentRepository;


    public IncidentDTO getIncident(Long incidentId){
        Incident incident = incidentRepository.findById(incidentId).orElseThrow(()-> new ResourceNotFoundException("Incident", "id", incidentId));

        return mapper.toDTO(incident);
    }


    public List<IncidentDTO> getAllIncidents(){
        List<Incident> incidents = incidentRepository.findAll();

        return incidents.stream().map(incident -> mapper.toDTO(incident)).toList();
    }
}
