package com.incidentplatform.audit.entity;

import com.incidentplatform.identity.entity.User;
import com.incidentplatform.incident.entity.Incident;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User actor;

    @ManyToOne(fetch = FetchType.LAZY)
    private Incident incident;

    @Column(nullable = false)
    private LocalDateTime timestamp;


    @PrePersist
    public void prePersist(){
        this.timestamp = LocalDateTime.now();
    }

    
}
