package com.aurealab.service.notification;

import com.aurealab.dto.notification.CreateNotificationRequestDTO;
import com.aurealab.dto.notification.NotificationDTO;
import com.aurealab.mapper.notification.NotificationMapper;
import com.aurealab.model.aurea.entity.UserEntity;
import com.aurealab.model.aurea.repository.UserRepository;
import com.aurealab.model.notification.entity.NotificationEntity;
import com.aurealab.model.notification.entity.UserNotificationEntity;
import com.aurealab.model.notification.repository.NotificationRepository;
import com.aurealab.model.notification.repository.UserNotificationRepository;
import com.aurealab.model.specs.UserNotificationSpecs;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationWebSocketService notificationWebSocketService;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getUserNotifications(Long userId, Pageable pageable, String category, Boolean isRead, String searchValue) {
        Specification<UserNotificationEntity> spec = UserNotificationSpecs.filter(userId, category, isRead, searchValue);
        Page<UserNotificationEntity> page = userNotificationRepository.findAll(spec, pageable);
        return page.map(NotificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        if (userId == null) return 0;
        return userNotificationRepository.countUnreadByUserId(userId);
    }

    @Override
    @Transactional
    public NotificationDTO markAsRead(Long userNotificationId, Long userId) {
        UserNotificationEntity userNotif = userNotificationRepository.findByIdAndUserIdWithNotification(userNotificationId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Notificación no encontrada para el usuario"));

        if (Boolean.FALSE.equals(userNotif.getIsRead()) || userNotif.getIsRead() == null) {
            userNotif.setIsRead(true);
            userNotif.setReadAt(OffsetDateTime.now());
            userNotif = userNotificationRepository.save(userNotif);

            // Actualizar conteo en vivo por WebSocket
            long unread = userNotificationRepository.countUnreadByUserId(userId);
            notificationWebSocketService.sendUnreadCountToUser(userId, unread);
        }

        return NotificationMapper.toDto(userNotif);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        if (userId == null) return;
        userNotificationRepository.markAllAsReadByUserId(userId, OffsetDateTime.now());
        // Enviar actualización de contador 0 en tiempo real
        notificationWebSocketService.sendUnreadCountToUser(userId, 0);
    }

    @Override
    @Transactional
    public NotificationDTO createNotification(CreateNotificationRequestDTO request) {
        // 1. Guardar la entidad Notification en su tabla
        NotificationEntity notif = NotificationEntity.builder()
                .title(request.title())
                .message(request.message())
                .category(request.category() != null ? request.category().toUpperCase() : "SECURITY")
                .priority(request.priority() != null ? request.priority().toUpperCase() : "INFO")
                .targetUrl(request.targetUrl())
                .createdAt(OffsetDateTime.now())
                .build();

        NotificationEntity savedNotif = notificationRepository.save(notif);

        // 2. Determinar destinatarios
        List<Long> targetUserIds = new ArrayList<>();
        if (request.userIds() != null && !request.userIds().isEmpty()) {
            targetUserIds.addAll(request.userIds());
        } else {
            // Si no se especifican usuarios, se asigna a todos los usuarios del sistema
            Iterable<UserEntity> allUsers = userRepository.findAll();
            for (UserEntity user : allUsers) {
                targetUserIds.add(user.getId());
            }
        }

        NotificationDTO lastCreatedDto = null;

        // 3. Crear registros en user_notification y emitir evento WebSocket
        for (Long userId : targetUserIds) {
            UserNotificationEntity userNotif = UserNotificationEntity.builder()
                    .notification(savedNotif)
                    .userId(userId)
                    .isRead(false)
                    .createdAt(savedNotif.getCreatedAt())
                    .build();

            UserNotificationEntity savedUserNotif = userNotificationRepository.save(userNotif);
            NotificationDTO dto = NotificationMapper.toDto(savedNotif, savedUserNotif);
            lastCreatedDto = dto;

            // Emitir en tiempo real al WebSocket del usuario
            notificationWebSocketService.sendNotificationToUser(userId, dto);

            // Actualizar badge de no leídas
            long unread = userNotificationRepository.countUnreadByUserId(userId);
            notificationWebSocketService.sendUnreadCountToUser(userId, unread);
        }

        return lastCreatedDto != null ? lastCreatedDto : NotificationMapper.toDto(savedNotif, null);
    }
}