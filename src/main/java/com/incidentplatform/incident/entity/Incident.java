package com.incidentplatform.incident.entity;


import com.incidentplatform.identity.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity(name = "incidents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;


    private String description;


    @Enumerated(EnumType.STRING)
    private IncidentSeverity severity;


    @Enumerated(EnumType.STRING)
    private IncidentStatus status = IncidentStatus.OPEN;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;



    private LocalDateTime resolvedAt;


    private LocalDateTime closedAt;


    @OneToMany(mappedBy = "incident",orphanRemoval = true,fetch = FetchType.LAZY)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "incident", orphanRemoval = true,fetch = FetchType.LAZY)
    private Set<Attachment> attachments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;


    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    @PrePersist
    private void prePersist(){
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
