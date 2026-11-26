package com.incidentplatform.identity.service;

import com.incidentplatform.common.Exception.ApiException;
import com.incidentplatform.common.Exception.ResourceNotFoundException;
import com.incidentplatform.common.dto.PageResponse;
import com.incidentplatform.common.utils.AuthUtils;
import com.incidentplatform.identity.dto.requests.UpdateProfileRequest;
import com.incidentplatform.identity.dto.requests.UpdateUserRoleRequest;
import com.incidentplatform.identity.dto.response.UserResponse;
import com.incidentplatform.identity.entity.User;
import com.incidentplatform.identity.mapper.UserMapper;
import com.incidentplatform.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthUtils authUtils;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() {
        return userMapper.toResponse(authUtils.getCurrentUser());
    }

    @Transactional
    public UserResponse updateCurrentUserProfile(UpdateProfileRequest request) {
        User currentUser = authUtils.getCurrentUser();
        if (!request.username().equalsIgnoreCase(currentUser.getUsername())
                && userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new ApiException("Username '" + request.username() + "' is already taken");
        }
        currentUser.setUsername(request.username());
        return userMapper.toResponse(userRepository.save(currentUser));
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        return PageResponse.from(userRepository.findAll(pageable).map(userMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        return userMapper.toResponse(findUserOrThrow(id));
    }

    @Transactional
    public UserResponse updateUserRole(Long id, UpdateUserRoleRequest request) {
        User user = findUserOrThrow(id);
        user.setRole(request.role());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findUserOrThrow(id);
        if (user.getId().equals(authUtils.getCurrentUserId())) {
            throw new ApiException("Cannot delete your own account");
        }
        userRepository.delete(user);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
