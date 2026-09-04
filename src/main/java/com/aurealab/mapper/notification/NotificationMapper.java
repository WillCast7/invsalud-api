package com.aurealab.mapper.notification;

import com.aurealab.dto.notification.NotificationDTO;
import com.aurealab.model.notification.entity.NotificationEntity;
import com.aurealab.model.notification.entity.UserNotificationEntity;

public class NotificationMapper {

    private NotificationMapper() {
    }

    public static NotificationDTO toDto(UserNotificationEntity userNotif) {
        if (userNotif == null) return null;

        NotificationEntity notif = userNotif.getNotification();
        return NotificationDTO.builder()
                .id(userNotif.getId())
                .notificationId(notif != null ? notif.getId() : null)
                .userId(userNotif.getUserId())
                .title(notif != null ? notif.getTitle() : null)
                .message(notif != null ? notif.getMessage() : null)
                .category(notif != null ? notif.getCategory() : null)
                .priority(notif != null ? notif.getPriority() : "INFO")
                .targetUrl(notif != null ? notif.getTargetUrl() : null)
                .createdAt(userNotif.getCreatedAt() != null ? userNotif.getCreatedAt() : (notif != null ? notif.getCreatedAt() : null))
                .isRead(userNotif.getIsRead() != null ? userNotif.getIsRead() : false)
                .readAt(userNotif.getReadAt())
                .build();
    }

    public static NotificationDTO toDto(NotificationEntity notif, UserNotificationEntity userNotif) {
        if (notif == null) return null;

        return NotificationDTO.builder()
                .id(userNotif != null ? userNotif.getId() : null)
                .notificationId(notif.getId())
                .userId(userNotif != null ? userNotif.getUserId() : null)
                .title(notif.getTitle())
                .message(notif.getMessage())
                .category(notif.getCategory())
                .priority(notif.getPriority() != null ? notif.getPriority() : "INFO")
                .targetUrl(notif.getTargetUrl())
                .createdAt(notif.getCreatedAt())
                .isRead(userNotif != null && userNotif.getIsRead() != null ? userNotif.getIsRead() : false)
                .readAt(userNotif != null ? userNotif.getReadAt() : null)
                .build();
    }
}