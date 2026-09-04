package com.aurealab.service.notification;

import com.aurealab.dto.notification.NotificationDTO;
import com.aurealab.dto.notification.UnreadCountResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NotificationWebSocketServiceImpl implements NotificationWebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendNotificationToUser(Long userId, NotificationDTO notification) {
        if (userId == null || notification == null) return;
        try {
            // Canal por usuario: /topic/notifications/{userId}
            String userTopic = "/topic/notifications/" + userId;
            messagingTemplate.convertAndSend(userTopic, notification);

            // Cola privada STOMP User: /user/{userId}/queue/notifications
            messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", notification);

            log.info("Notificación emitida vía WebSocket a usuario ID {}: {}", userId, notification.title());
        } catch (Exception e) {
            log.error("Error enviando notificación WebSocket a usuario {}: {}", userId, e.getMessage());
        }
    }

    @Override
    public void sendNotificationToUsers(List<Long> userIds, NotificationDTO notification) {
        if (userIds == null || userIds.isEmpty()) {
            broadcastNotification(notification);
            return;
        }
        for (Long userId : userIds) {
            sendNotificationToUser(userId, notification);
        }
    }

    @Override
    public void sendUnreadCountToUser(Long userId, long unreadCount) {
        if (userId == null) return;
        try {
            UnreadCountResponseDTO countDto = new UnreadCountResponseDTO(unreadCount);
            messagingTemplate.convertAndSend("/topic/notifications/" + userId + "/count", countDto);
            messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications/count", countDto);
        } catch (Exception e) {
            log.error("Error enviando conteo de no leídas a usuario {}: {}", userId, e.getMessage());
        }
    }

    @Override
    public void broadcastNotification(NotificationDTO notification) {
        if (notification == null) return;
        try {
            messagingTemplate.convertAndSend("/topic/notifications/global", notification);
            messagingTemplate.convertAndSend("/topic/notifications", notification);
            log.info("Notificación global emitida vía WebSocket: {}", notification.title());
        } catch (Exception e) {
            log.error("Error transmitiendo notificación global: {}", e.getMessage());
        }
    }
}