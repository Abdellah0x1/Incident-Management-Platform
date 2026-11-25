package com.incidentplatform.notification.service;

import com.incidentplatform.common.Exception.ResourceNotFoundException;
import com.incidentplatform.common.dto.PageResponse;
import com.incidentplatform.common.utils.AuthUtils;
import com.incidentplatform.notification.dto.NotificationResponse;
import com.incidentplatform.notification.entity.Notification;
import com.incidentplatform.notification.mapper.NotificationMapper;
import com.incidentplatform.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper mapper;
    private final AuthUtils authUtils;

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getMyNotifications(boolean unreadOnly, Pageable pageable) {
        Long userId = authUtils.getCurrentUserId();
        Page<Notification> page = unreadOnly
                ? notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable)
                : notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.from(page.map(mapper::toResponse));
    }

    @Transactional(readOnly = true)
    public long getUnreadCount() {
        return notificationRepository.countByUserIdAndIsReadFalse(authUtils.getCurrentUserId());
    }

    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        Long currentUserId = authUtils.getCurrentUserId();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You cannot modify another user's notification");
        }

        notification.setRead(true);
        return mapper.toResponse(notificationRepository.save(notification));
    }

    @Transactional
    public void markAllAsRead() {
        Long currentUserId = authUtils.getCurrentUserId();
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(currentUserId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}
