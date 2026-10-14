package com.incidentplatform.incident.repository;


import com.incidentplatform.incident.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRepository extends JpaRepository<Incident,Long> {

    Incident getIncidentById(Long id);
}
