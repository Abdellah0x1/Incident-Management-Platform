package com.incidentplatform.team.entity;

import com.incidentplatform.identity.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "teams")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;


    @ManyToMany(mappedBy = "teams",fetch = FetchType.LAZY)
    private Set<User> members;
}
