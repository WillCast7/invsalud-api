package com.aurealab.dto.notification;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import java.util.List;

@Builder
public record CreateNotificationRequestDTO(
    @NotBlank(message = "El título es obligatorio")
    String title,

    @NotBlank(message = "El mensaje es obligatorio")
    String message,

    @NotBlank(message = "La categoría es obligatoria")
    String category, // 'SECURITY', 'EXPIRATION_MEDICINE', 'CONTRACT'

    String priority, // 'INFO', 'WARNING', 'CRITICAL'
    String targetUrl,
    List<Long> userIds // Si es null o vacío, puede enviarse a todos o requerirse
) {
}