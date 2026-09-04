package com.aurealab.service.notification;

import com.aurealab.dto.notification.NotificationDTO;

import java.util.List;

public interface NotificationWebSocketService {
    void sendNotificationToUser(Long userId, NotificationDTO notification);
    void sendNotificationToUsers(List<Long> userIds, NotificationDTO notification);
    void sendUnreadCountToUser(Long userId, long unreadCount);
    void broadcastNotification(NotificationDTO notification);
}