package com.incidentplatform.team.mapper;

import com.incidentplatform.incident.dto.response.UserSummary;
import com.incidentplatform.team.dto.response.TeamResponse;
import com.incidentplatform.team.dto.response.TeamSummaryResponse;
import com.incidentplatform.team.entity.Team;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamMapper {

    public TeamResponse toResponse(Team team) {
        List<UserSummary> members = team.getMembers() == null
                ? List.of()
                : team.getMembers().stream()
                .map(u -> new UserSummary(u.getId(), u.getUsername(), u.getEmail()))
                .toList();

        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getDescription(),
                members,
                team.getCreatedAt()
        );
    }

    public TeamSummaryResponse toSummary(Team team) {
        int count = team.getMembers() == null ? 0 : team.getMembers().size();
        return new TeamSummaryResponse(
                team.getId(),
                team.getName(),
                team.getDescription(),
                count,
                team.getCreatedAt()
        );
    }
}
