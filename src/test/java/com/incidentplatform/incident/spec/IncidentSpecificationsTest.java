package com.incidentplatform.incident.spec;

import com.incidentplatform.incident.entity.IncidentSeverity;
import com.incidentplatform.incident.entity.IncidentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class IncidentSpecificationsTest {

    @Test
    void nullFiltersProduceNullPredicatesSafely() {
        assertNull(IncidentSpecifications.hasStatus(null).toPredicate(null, null, null));
        assertNull(IncidentSpecifications.hasSeverity(null).toPredicate(null, null, null));
        assertNull(IncidentSpecifications.assignedTo(null).toPredicate(null, null, null));
        assertNull(IncidentSpecifications.forService(null).toPredicate(null, null, null));
        assertNull(IncidentSpecifications.createdBetween(null, null).toPredicate(null, null, null));
        assertNull(IncidentSpecifications.searchKeyword(null).toPredicate(null, null, null));
        assertNull(IncidentSpecifications.searchKeyword("   ").toPredicate(null, null, null));
    }

    @Test
    void allOfComposesMixedNullAndNonNullSpecifications() {
        Specification<?> spec = Specification.allOf(
                IncidentSpecifications.hasStatus(IncidentStatus.OPEN),
                IncidentSpecifications.hasSeverity(IncidentSeverity.HIGH),
                IncidentSpecifications.assignedTo(null),
                IncidentSpecifications.createdBetween(LocalDateTime.now().minusDays(1), null),
                IncidentSpecifications.searchKeyword("db")
        );
        assertNotNull(spec);
    }
}
