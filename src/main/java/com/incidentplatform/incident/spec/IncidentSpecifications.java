package com.incidentplatform.incident.spec;

import com.incidentplatform.incident.entity.Incident;
import com.incidentplatform.incident.entity.IncidentSeverity;
import com.incidentplatform.incident.entity.IncidentStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class IncidentSpecifications {

    private IncidentSpecifications() {
    }

    public static Specification<Incident> hasStatus(IncidentStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Incident> hasSeverity(IncidentSeverity severity) {
        return (root, query, cb) -> severity == null ? null : cb.equal(root.get("severity"), severity);
    }

    public static Specification<Incident> assignedTo(Long assignedToId) {
        return (root, query, cb) -> assignedToId == null ? null : cb.equal(root.get("assignedTo").get("id"), assignedToId);
    }

    public static Specification<Incident> forService(Long serviceId) {
        return (root, query, cb) -> serviceId == null ? null : cb.equal(root.get("service").get("id"), serviceId);
    }

    public static Specification<Incident> createdBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from == null && to == null) {
                return null;
            }
            if (from != null && to != null) {
                return cb.between(root.get("createdAt"), from, to);
            }
            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), from);
            }
            return cb.lessThanOrEqualTo(root.get("createdAt"), to);
        };
    }

    public static Specification<Incident> searchKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }
}
