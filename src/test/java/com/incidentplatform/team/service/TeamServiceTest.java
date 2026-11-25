package com.incidentplatform.team.service;

import com.incidentplatform.common.Exception.ApiException;
import com.incidentplatform.common.Exception.ResourceNotFoundException;
import com.incidentplatform.identity.entity.User;
import com.incidentplatform.identity.repository.UserRepository;
import com.incidentplatform.team.dto.request.CreateTeamRequest;
import com.incidentplatform.team.dto.request.UpdateTeamRequest;
import com.incidentplatform.team.dto.response.TeamResponse;
import com.incidentplatform.team.entity.Team;
import com.incidentplatform.team.mapper.TeamMapper;
import com.incidentplatform.team.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class TeamServiceTest {

    private Map<Long, Team> teamStore;
    private Map<Long, User> userStore;
    private AtomicLong idSeq;
    private TeamService teamService;

    @BeforeEach
    void setUp() {
        teamStore = new HashMap<>();
        userStore = new HashMap<>();
        idSeq = new AtomicLong(1L);

        TeamRepository teamRepository = (TeamRepository) Proxy.newProxyInstance(
                TeamRepository.class.getClassLoader(),
                new Class<?>[]{TeamRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "save" -> {
                        Team t = (Team) args[0];
                        if (t.getId() == null) {
                            t.setId(idSeq.getAndIncrement());
                        }
                        teamStore.put(t.getId(), t);
                        yield t;
                    }
                    case "findById" -> Optional.ofNullable(teamStore.get(args[0]));
                    case "findAll" -> teamStore.values().stream().toList();
                    case "existsByNameIgnoreCase" -> teamStore.values().stream()
                            .anyMatch(t -> t.getName().equalsIgnoreCase((String) args[0]));
                    case "delete" -> {
                        Team t = (Team) args[0];
                        teamStore.remove(t.getId());
                        yield null;
                    }
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );

        UserRepository userRepository = (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class<?>[]{UserRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "findById" -> Optional.ofNullable(userStore.get(args[0]));
                    case "save" -> {
                        User u = (User) args[0];
                        userStore.put(u.getId(), u);
                        yield u;
                    }
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );

        teamService = new TeamService(teamRepository, userRepository, new TeamMapper());
    }

    @Test
    void createTeamRejectsDuplicateName() {
        teamService.createTeam(new CreateTeamRequest("SRE", "Site Reliability"));

        assertThrows(ApiException.class,
                () -> teamService.createTeam(new CreateTeamRequest("sre", "Duplicate")));
    }

    @Test
    void addAndRemoveMemberSyncsOwningSideAndMembers() {
        TeamResponse created = teamService.createTeam(new CreateTeamRequest("Platform", "Core infra"));
        User user = new User();
        user.setId(10L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setTeams(new HashSet<>());
        userStore.put(10L, user);

        TeamResponse afterAdd = teamService.addMember(created.id(), 10L);
        assertEquals(1, afterAdd.members().size());
        assertEquals(1, user.getTeams().size());

        TeamResponse afterRemove = teamService.removeMember(created.id(), 10L);
        assertTrue(afterRemove.members().isEmpty());
        assertTrue(user.getTeams().isEmpty());
    }

    @Test
    void updateTeamModifiesFields() {
        TeamResponse created = teamService.createTeam(new CreateTeamRequest("Backend", "API team"));
        TeamResponse updated = teamService.updateTeam(created.id(), new UpdateTeamRequest("Core API", "Updated"));

        assertEquals("Core API", updated.name());
        assertEquals("Updated", updated.description());
    }

    @Test
    void getTeamNotFoundThrowsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> teamService.getTeam(404L));
    }
}
