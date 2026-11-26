package com.incidentplatform.identity.service;

import com.incidentplatform.common.Exception.ApiException;
import com.incidentplatform.common.Exception.ResourceNotFoundException;
import com.incidentplatform.common.dto.PageResponse;
import com.incidentplatform.common.utils.AuthUtils;
import com.incidentplatform.identity.dto.requests.UpdateProfileRequest;
import com.incidentplatform.identity.dto.requests.UpdateUserRoleRequest;
import com.incidentplatform.identity.dto.response.UserResponse;
import com.incidentplatform.identity.entity.ROLE;
import com.incidentplatform.identity.entity.User;
import com.incidentplatform.identity.mapper.UserMapper;
import com.incidentplatform.identity.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private Map<Long, User> userStore;
    private User currentUser;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userStore = new HashMap<>();

        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("alice");
        currentUser.setEmail("alice@example.com");
        currentUser.setRole(ROLE.ADMIN);
        currentUser.setCreatedAt(LocalDateTime.now());
        userStore.put(1L, currentUser);

        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("bob");
        otherUser.setEmail("bob@example.com");
        otherUser.setRole(ROLE.VIEWER);
        otherUser.setCreatedAt(LocalDateTime.now());
        userStore.put(2L, otherUser);

        UserRepository userRepository = (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class<?>[]{UserRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "findById" -> Optional.ofNullable(userStore.get(args[0]));
                    case "findAll" -> {
                        Pageable pageable = (Pageable) args[0];
                        List<User> all = userStore.values().stream().toList();
                        yield new PageImpl<>(all, pageable, all.size());
                    }
                    case "existsByUsernameIgnoreCase" -> userStore.values().stream()
                            .anyMatch(u -> u.getUsername().equalsIgnoreCase((String) args[0]));
                    case "save" -> {
                        User u = (User) args[0];
                        userStore.put(u.getId(), u);
                        yield u;
                    }
                    case "delete" -> {
                        User u = (User) args[0];
                        userStore.remove(u.getId());
                        yield null;
                    }
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );

        AuthUtils authUtils = new AuthUtils(userRepository) {
            @Override
            public User getCurrentUser() {
                return currentUser;
            }

            @Override
            public Long getCurrentUserId() {
                return currentUser.getId();
            }
        };

        userService = new UserService(userRepository, new UserMapper(), authUtils);
    }

    @Test
    void getCurrentUserProfileReturnsCurrentUser() {
        UserResponse response = userService.getCurrentUserProfile();
        assertEquals(1L, response.id());
        assertEquals("alice", response.username());
        assertEquals("alice@example.com", response.email());
        assertEquals(ROLE.ADMIN, response.role());
    }

    @Test
    void updateCurrentUserProfileUpdatesUsernameAndRejectsDuplicate() {
        UserResponse updated = userService.updateCurrentUserProfile(new UpdateProfileRequest("alice-updated"));
        assertEquals("alice-updated", updated.username());

        assertThrows(ApiException.class,
                () -> userService.updateCurrentUserProfile(new UpdateProfileRequest("BOB")));
    }

    @Test
    void getAllUsersReturnsPagedResponse() {
        PageResponse<UserResponse> page = userService.getAllUsers(PageRequest.of(0, 10));
        assertEquals(2, page.totalElements());
        assertEquals(2, page.content().size());
    }

    @Test
    void getUserThrowsWhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> userService.getUser(999L));
    }

    @Test
    void updateUserRoleChangesRole() {
        UserResponse updated = userService.updateUserRole(2L, new UpdateUserRoleRequest(ROLE.INCIDENT_MANAGER));
        assertEquals(ROLE.INCIDENT_MANAGER, updated.role());
    }

    @Test
    void deleteUserRejectsSelfDeletionAndDeletesOtherUser() {
        assertThrows(ApiException.class, () -> userService.deleteUser(1L));

        userService.deleteUser(2L);
        assertFalse(userStore.containsKey(2L));
    }
}
