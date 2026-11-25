package com.incidentplatform.team.service;

import com.incidentplatform.common.Exception.ApiException;
import com.incidentplatform.common.Exception.ResourceNotFoundException;
import com.incidentplatform.common.dto.PageResponse;
import com.incidentplatform.identity.entity.User;
import com.incidentplatform.identity.repository.UserRepository;
import com.incidentplatform.team.dto.request.CreateTeamRequest;
import com.incidentplatform.team.dto.request.UpdateTeamRequest;
import com.incidentplatform.team.dto.response.TeamResponse;
import com.incidentplatform.team.dto.response.TeamSummaryResponse;
import com.incidentplatform.team.entity.Team;
import com.incidentplatform.team.mapper.TeamMapper;
import com.incidentplatform.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMapper mapper;

    @Transactional
    public TeamResponse createTeam(CreateTeamRequest request) {
        if (teamRepository.existsByNameIgnoreCase(request.name())) {
            throw new ApiException("Team with name '" + request.name() + "' already exists");
        }

        Team team = new Team();
        team.setName(request.name());
        team.setDescription(request.description());
        team.setMembers(new HashSet<>());

        return mapper.toResponse(teamRepository.save(team));
    }

    @Transactional(readOnly = true)
    public TeamResponse getTeam(Long teamId) {
        return mapper.toResponse(findTeamOrThrow(teamId));
    }

    @Transactional(readOnly = true)
    public PageResponse<TeamSummaryResponse> getAllTeams(Pageable pageable) {
        return PageResponse.from(teamRepository.findAll(pageable).map(mapper::toSummary));
    }

    @Transactional
    public TeamResponse updateTeam(Long teamId, UpdateTeamRequest request) {
        Team team = findTeamOrThrow(teamId);

        if (request.name() != null && !request.name().isBlank()) {
            if (!request.name().equalsIgnoreCase(team.getName())
                    && teamRepository.existsByNameIgnoreCase(request.name())) {
                throw new ApiException("Team with name '" + request.name() + "' already exists");
            }
            team.setName(request.name());
        }
        if (request.description() != null) {
            team.setDescription(request.description());
        }

        return mapper.toResponse(teamRepository.save(team));
    }

    @Transactional
    public void deleteTeam(Long teamId) {
        Team team = findTeamOrThrow(teamId);
        if (team.getMembers() != null) {
            for (User member : new HashSet<>(team.getMembers())) {
                if (member.getTeams() != null) {
                    member.getTeams().remove(team);
                    userRepository.save(member);
                }
            }
        }
        teamRepository.delete(team);
    }

    @Transactional
    public TeamResponse addMember(Long teamId, Long userId) {
        Team team = findTeamOrThrow(teamId);
        User user = findUserOrThrow(userId);

        if (user.getTeams() == null) {
            user.setTeams(new HashSet<>());
        }
        user.getTeams().add(team);
        userRepository.save(user);

        if (team.getMembers() == null) {
            team.setMembers(new HashSet<>());
        }
        team.getMembers().add(user);

        return mapper.toResponse(team);
    }

    @Transactional
    public TeamResponse removeMember(Long teamId, Long userId) {
        Team team = findTeamOrThrow(teamId);
        User user = findUserOrThrow(userId);

        if (user.getTeams() != null) {
            user.getTeams().remove(team);
            userRepository.save(user);
        }
        if (team.getMembers() != null) {
            team.getMembers().remove(user);
        }

        return mapper.toResponse(team);
    }

    private Team findTeamOrThrow(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
