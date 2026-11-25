package com.incidentplatform.team.controller;

import com.incidentplatform.common.dto.PageResponse;
import com.incidentplatform.team.dto.request.CreateTeamRequest;
import com.incidentplatform.team.dto.request.UpdateTeamRequest;
import com.incidentplatform.team.dto.response.TeamResponse;
import com.incidentplatform.team.dto.response.TeamSummaryResponse;
import com.incidentplatform.team.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<TeamSummaryResponse>> getAllTeams(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(teamService.getAllTeams(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeam(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeam(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTeamRequest request) {
        return ResponseEntity.ok(teamService.updateTeam(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/members/{userId}")
    public ResponseEntity<TeamResponse> addMember(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(teamService.addMember(id, userId));
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<TeamResponse> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(teamService.removeMember(id, userId));
    }
}
