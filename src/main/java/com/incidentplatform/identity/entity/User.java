package com.incidentplatform.identity.entity;

import com.incidentplatform.team.entity.Team;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(nullable = false)
    @Size(min = 8, message = "password must contain at least 8 characters")
    private String password;



    @Enumerated(EnumType.STRING)
    private ROLE role;

    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_teams", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "team_id"))
    private Set<Team> teams;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}
