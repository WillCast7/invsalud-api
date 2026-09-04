package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.notification.CreateNotificationRequestDTO;
import com.aurealab.dto.notification.NotificationDTO;
import com.aurealab.dto.notification.UnreadCountResponseDTO;
import com.aurealab.service.notification.NotificationService;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private com.aurealab.service.notification.ExpirationAlertService expirationAlertService;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<String>> getMyNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(defaultValue = "") String searchValue) {

        Long userId = jwtUtils.getCurrentUserId();
        if (userId == null) {
            userId = 1L; // Fallback por defecto si no hay sesión
        }

        int pageNumber = Math.max(0, page > 0 ? page - 1 : page);
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by("createdAt").descending());

        Page<NotificationDTO> result = notificationService.getUserNotifications(userId, pageable, category, isRead, searchValue);
        return ResponseEntity.ok(APIResponseDTO.withPageable(constants.success.findedSuccess, constants.success.findedSuccess, result));
    }

    @GetMapping(value = "/unread-count", produces = "application/json")
    public ResponseEntity<APIResponseDTO<UnreadCountResponseDTO>> getUnreadCount() {
        Long userId = jwtUtils.getCurrentUserId();
        if (userId == null) {
            userId = 1L;
        }

        long unreadCount = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(APIResponseDTO.success(new UnreadCountResponseDTO(unreadCount), constants.success.findedSuccess));
    }

    @RequestMapping(value = "/{id}/read", method = {RequestMethod.PATCH, RequestMethod.PUT}, produces = "application/json")
    public ResponseEntity<APIResponseDTO<NotificationDTO>> markNotificationAsRead(@PathVariable Long id) {
        Long userId = jwtUtils.getCurrentUserId();
        if (userId == null) {
            userId = 1L;
        }

        NotificationDTO result = notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(APIResponseDTO.success(result, constants.success.updatedSuccess));
    }

    @RequestMapping(value = "/read-all", method = {RequestMethod.PATCH, RequestMethod.PUT}, produces = "application/json")
    public ResponseEntity<APIResponseDTO<String>> markAllNotificationsAsRead() {
        Long userId = jwtUtils.getCurrentUserId();
        if (userId == null) {
            userId = 1L;
        }

        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(APIResponseDTO.success("Todas las notificaciones fueron marcadas como leídas", constants.success.updatedSuccess));
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<NotificationDTO>> createNotification(@RequestBody @Valid CreateNotificationRequestDTO request) {
        NotificationDTO result = notificationService.createNotification(request);
        return ResponseEntity.ok(APIResponseDTO.success(result, constants.success.savedSuccess));
    }

    @PostMapping(value = {"/trigger-test", "/test"}, produces = "application/json")
    public ResponseEntity<APIResponseDTO<NotificationDTO>> triggerTestNotification(
            @RequestBody(required = false) CreateNotificationRequestDTO request) {

        if (request == null || request.title() == null || request.title().isBlank()) {
            request = CreateNotificationRequestDTO.builder()
                    .title("Alerta de Prueba Postman")
                    .message("Esta es una notificación de prueba en tiempo real emitida vía WebSocket.")
                    .category("EXPIRATION_MEDICINE")
                    .priority("WARNING")
                    .targetUrl("/inventario/gestion/vencidos")
                    .build();
        }

        NotificationDTO result = notificationService.createNotification(request);
        return ResponseEntity.ok(APIResponseDTO.success(result, constants.success.savedSuccess));
    }

    @PostMapping(value = "/check-alerts", produces = "application/json")
    public ResponseEntity<APIResponseDTO<String>> checkExpirationAlerts(
            @RequestParam(defaultValue = "true") boolean includeRange) {
        int alertsCount = expirationAlertService.checkAndTriggerExpirationAlerts(includeRange);
        return ResponseEntity.ok(APIResponseDTO.success(
                "Escaneo de vencimientos completado. Se generaron " + alertsCount + " alertas nuevas.",
                constants.success.findedSuccess
        ));
    }
}