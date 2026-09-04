package com.aurealab.service.notification;

import com.aurealab.dto.notification.CreateNotificationRequestDTO;
import com.aurealab.dto.notification.NotificationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    Page<NotificationDTO> getUserNotifications(Long userId, Pageable pageable, String category, Boolean isRead, String searchValue);
    long getUnreadCount(Long userId);
    NotificationDTO markAsRead(Long userNotificationId, Long userId);
    void markAllAsRead(Long userId);
    NotificationDTO createNotification(CreateNotificationRequestDTO request);
}