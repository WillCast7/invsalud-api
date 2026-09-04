package com.aurealab.dto.notification;

import lombok.Builder;
import java.time.OffsetDateTime;

@Builder
public record NotificationDTO(
    Long id,               // ID de user_notification
    Long notificationId,   // ID de notification
    Long userId,           // ID del usuario
    String title,
    String message,
    String category,
    String priority,
    String targetUrl,
    OffsetDateTime createdAt,
    Boolean isRead,
    OffsetDateTime readAt
) {
}