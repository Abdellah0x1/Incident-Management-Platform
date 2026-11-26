package com.incidentplatform.identity.mapper;

import com.incidentplatform.identity.dto.response.UserResponse;
import com.incidentplatform.identity.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
