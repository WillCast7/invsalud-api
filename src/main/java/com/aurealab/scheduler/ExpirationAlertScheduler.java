package com.aurealab.scheduler;

import com.aurealab.service.notification.ExpirationAlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExpirationAlertScheduler {

    @Autowired
    private ExpirationAlertService expirationAlertService;

    /**
     * Ejecuta diariamente a las 8:00 AM (hora Colombia) el chequeo de vencimientos
     * de medicamentos (6 meses, 3 meses, hoy) y resoluciones (3 meses).
     */
    @Scheduled(cron = "0 0 8 * * *", zone = "America/Bogota")
    public void runDailyExpirationCheck() {
        log.info("Ejecutando tarea programada: Escaneo diario de vencimientos...");
        try {
            int alertsCount = expirationAlertService.checkAndTriggerExpirationAlerts();
            log.info("Tarea programada de vencimientos completada. Alertas enviadas: {}", alertsCount);
        } catch (Exception e) {
            log.error("Error al ejecutar la tarea programada de vencimientos", e);
        }
    }
}
