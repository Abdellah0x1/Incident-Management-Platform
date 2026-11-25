package com.incidentplatform.notification.controller;

import com.incidentplatform.common.dto.MessageResponse;
import com.incidentplatform.notification.dto.NotificationResponse;
import com.incidentplatform.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        return ResponseEntity.ok(notificationService.getMyNotifications(unreadOnly));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        return ResponseEntity.ok(Map.of("unreadCount", notificationService.getUnreadCount()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<MessageResponse> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok(new MessageResponse("All notifications marked as read"));
    }
}
