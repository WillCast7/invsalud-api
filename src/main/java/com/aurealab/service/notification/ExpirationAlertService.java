package com.aurealab.service.notification;

import com.aurealab.dto.notification.CreateNotificationRequestDTO;
import com.aurealab.model.inventory.entity.PrescriptionInventoryEntity;
import com.aurealab.model.inventory.entity.ProductEntity;
import com.aurealab.model.inventory.entity.ResolutionEntity;
import com.aurealab.model.inventory.repository.PrescriptionInventoryRepository;
import com.aurealab.model.inventory.repository.ResolutionRepository;
import com.aurealab.model.notification.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
public class ExpirationAlertService {

    @Autowired
    private PrescriptionInventoryRepository prescriptionInventoryRepository;

    @Autowired
    private ResolutionRepository resolutionRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public int checkAndTriggerExpirationAlerts() {
        return checkAndTriggerExpirationAlerts(false);
    }

    @Transactional
    public int checkAndTriggerExpirationAlerts(boolean includeRange) {
        int alertsCreated = 0;
        LocalDate today = LocalDate.now();
        OffsetDateTime antiDuplicateThreshold = OffsetDateTime.now().minusHours(20);

        log.info("Iniciando escaneo de alertas de vencimiento (fecha hoy: {}, includeRange: {})", today, includeRange);

        // 1. Escaneo de Medicamentos en Inventario
        List<PrescriptionInventoryEntity> activeStock = prescriptionInventoryRepository.findAllActiveStock();
        for (PrescriptionInventoryEntity inventory : activeStock) {
            LocalDate exp = inventory.getExpirationDate();
            if (exp == null) continue;

            ProductEntity product = inventory.getProduct();
            String productName = (product != null && product.getName() != null) ? product.getName() : "Medicamento";
            String productCode = (product != null && product.getCode() != null) ? product.getCode() : "";
            String batchCode = (inventory.getBatch() != null && inventory.getBatch().getCode() != null)
                    ? inventory.getBatch().getCode() : "N/A";
            int units = inventory.getAvailableUnits();

            String title = null;
            String message = null;
            String priority = "INFO";

            if (exp.isEqual(today) || (includeRange && !exp.isAfter(today))) {
                title = "🚨 Medicamento Vence Hoy: " + productName;
                message = String.format(
                        "El medicamento %s (Código: %s, Lote: %s) VENCE HOY (%s). Quedan %d unidades disponibles.",
                        productName, productCode, batchCode, exp, units
                );
                priority = "CRITICAL";
            } else if (exp.isEqual(today.plusMonths(3)) || (includeRange && exp.isAfter(today) && !exp.isAfter(today.plusMonths(3)))) {
                title = "⚠️ Medicamento por Vencer (3 Meses): " + productName;
                message = String.format(
                        "El medicamento %s (Código: %s, Lote: %s) vencerá el %s (3 meses o menos). Quedan %d unidades disponibles.",
                        productName, productCode, batchCode, exp, units
                );
                priority = "WARNING";
            } else if (exp.isEqual(today.plusMonths(6)) || (includeRange && exp.isAfter(today.plusMonths(3)) && !exp.isAfter(today.plusMonths(6)))) {
                title = "ℹ️ Medicamento por Vencer (6 Meses): " + productName;
                message = String.format(
                        "El medicamento %s (Código: %s, Lote: %s) vencerá el %s (6 meses). Quedan %d unidades disponibles.",
                        productName, productCode, batchCode, exp, units
                );
                priority = "INFO";
            }

            if (title != null) {
                // Verificar anti-duplicado en las últimas 20 horas
                if (!notificationRepository.existsSimilarNotification("EXPIRATION_MEDICINE", title, antiDuplicateThreshold)) {
                    notificationService.createNotification(CreateNotificationRequestDTO.builder()
                            .title(title)
                            .message(message)
                            .category("EXPIRATION_MEDICINE")
                            .priority(priority)
                            .targetUrl("/inventory/medicines")
                            .userIds(null)
                            .build());
                    alertsCreated++;
                    log.info("Alerta creada: {}", title);
                }
            }
        }

        // 2. Escaneo de Resoluciones
        List<ResolutionEntity> activeResolutions = resolutionRepository.findByIsActiveTrue();
        for (ResolutionEntity resolution : activeResolutions) {
            LocalDate exp = resolution.getExpirationDate();
            if (exp == null) continue;

            if (exp.isEqual(today.plusMonths(3)) || (includeRange && exp.isAfter(today) && !exp.isAfter(today.plusMonths(3)))) {
                String thirdPartyName = (resolution.getThirdParty() != null && resolution.getThirdParty().getFullName() != null)
                        ? resolution.getThirdParty().getFullName() : "N/A";
                String title = "⚠️ Resolución Próxima a Vencer (3 Meses): Nº " + resolution.getCode();
                String message = String.format(
                        "La resolución Nº %s del tercero %s vencerá el %s.",
                        resolution.getCode(), thirdPartyName, exp
                );

                if (!notificationRepository.existsSimilarNotification("CONTRACT", title, antiDuplicateThreshold)) {
                    notificationService.createNotification(CreateNotificationRequestDTO.builder()
                            .title(title)
                            .message(message)
                            .category("CONTRACT")
                            .priority("WARNING")
                            .targetUrl("/inventory/third-parties")
                            .userIds(null)
                            .build());
                    alertsCreated++;
                    log.info("Alerta creada: {}", title);
                }
            }
        }

        log.info("Escaneo completado. Total alertas generadas: {}", alertsCreated);
        return alertsCreated;
    }
}
