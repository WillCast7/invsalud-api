package com.aurealab.service;

import com.aurealab.dto.notification.CreateNotificationRequestDTO;
import com.aurealab.model.inventory.entity.BatchEntity;
import com.aurealab.model.inventory.entity.PrescriptionInventoryEntity;
import com.aurealab.model.inventory.entity.ProductEntity;
import com.aurealab.model.inventory.entity.ResolutionEntity;
import com.aurealab.model.inventory.entity.ThirdPartyEntity;
import com.aurealab.model.inventory.repository.PrescriptionInventoryRepository;
import com.aurealab.model.inventory.repository.ResolutionRepository;
import com.aurealab.model.notification.repository.NotificationRepository;
import com.aurealab.service.notification.ExpirationAlertService;
import com.aurealab.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockAndExpirationAlertTest {

    @Mock
    private PrescriptionInventoryRepository prescriptionInventoryRepository;

    @Mock
    private ResolutionRepository resolutionRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ExpirationAlertService expirationAlertService;

    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
    }

    @Test
    void testMedicineExpiringTodayTriggersCriticalAlert() {
        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setName("Morfina 10mg");
        product.setCode("MED-001");
        product.setUnitsAlert(5);

        BatchEntity batch = new BatchEntity();
        batch.setCode("LOT-2026-A");

        PrescriptionInventoryEntity inventory = new PrescriptionInventoryEntity();
        inventory.setId(101L);
        inventory.setProduct(product);
        inventory.setBatch(batch);
        inventory.setAvailableUnits(15);
        inventory.setExpirationDate(today);

        when(prescriptionInventoryRepository.findAllActiveStock()).thenReturn(List.of(inventory));
        when(resolutionRepository.findByIsActiveTrue()).thenReturn(List.of());
        when(notificationRepository.existsSimilarNotification(eq("EXPIRATION_MEDICINE"), any(), any(OffsetDateTime.class)))
                .thenReturn(false);

        int alerts = expirationAlertService.checkAndTriggerExpirationAlerts(false);

        assertEquals(1, alerts);
        ArgumentCaptor<CreateNotificationRequestDTO> captor = ArgumentCaptor.forClass(CreateNotificationRequestDTO.class);
        verify(notificationService, times(1)).createNotification(captor.capture());

        CreateNotificationRequestDTO req = captor.getValue();
        assertTrue(req.title().contains("🚨 Medicamento Vence Hoy"));
        assertEquals("CRITICAL", req.priority());
        assertEquals("EXPIRATION_MEDICINE", req.category());
    }

    @Test
    void testMedicineExpiringIn3MonthsTriggersWarningAlert() {
        ProductEntity product = new ProductEntity();
        product.setId(2L);
        product.setName("Fentanilo 0.5mg");
        product.setCode("MED-002");

        BatchEntity batch = new BatchEntity();
        batch.setCode("LOT-3M");

        PrescriptionInventoryEntity inventory = new PrescriptionInventoryEntity();
        inventory.setId(102L);
        inventory.setProduct(product);
        inventory.setBatch(batch);
        inventory.setAvailableUnits(20);
        inventory.setExpirationDate(today.plusMonths(3));

        when(prescriptionInventoryRepository.findAllActiveStock()).thenReturn(List.of(inventory));
        when(resolutionRepository.findByIsActiveTrue()).thenReturn(List.of());
        when(notificationRepository.existsSimilarNotification(eq("EXPIRATION_MEDICINE"), any(), any(OffsetDateTime.class)))
                .thenReturn(false);

        int alerts = expirationAlertService.checkAndTriggerExpirationAlerts(false);

        assertEquals(1, alerts);
        ArgumentCaptor<CreateNotificationRequestDTO> captor = ArgumentCaptor.forClass(CreateNotificationRequestDTO.class);
        verify(notificationService, times(1)).createNotification(captor.capture());

        CreateNotificationRequestDTO req = captor.getValue();
        assertTrue(req.title().contains("⚠️ Medicamento por Vencer (3 Meses)"));
        assertEquals("WARNING", req.priority());
    }

    @Test
    void testMedicineExpiringIn6MonthsTriggersInfoAlert() {
        ProductEntity product = new ProductEntity();
        product.setId(3L);
        product.setName("Oxicodona 20mg");
        product.setCode("MED-003");

        BatchEntity batch = new BatchEntity();
        batch.setCode("LOT-6M");

        PrescriptionInventoryEntity inventory = new PrescriptionInventoryEntity();
        inventory.setId(103L);
        inventory.setProduct(product);
        inventory.setBatch(batch);
        inventory.setAvailableUnits(50);
        inventory.setExpirationDate(today.plusMonths(6));

        when(prescriptionInventoryRepository.findAllActiveStock()).thenReturn(List.of(inventory));
        when(resolutionRepository.findByIsActiveTrue()).thenReturn(List.of());
        when(notificationRepository.existsSimilarNotification(eq("EXPIRATION_MEDICINE"), any(), any(OffsetDateTime.class)))
                .thenReturn(false);

        int alerts = expirationAlertService.checkAndTriggerExpirationAlerts(false);

        assertEquals(1, alerts);
        ArgumentCaptor<CreateNotificationRequestDTO> captor = ArgumentCaptor.forClass(CreateNotificationRequestDTO.class);
        verify(notificationService, times(1)).createNotification(captor.capture());

        CreateNotificationRequestDTO req = captor.getValue();
        assertTrue(req.title().contains("ℹ️ Medicamento por Vencer (6 Meses)"));
        assertEquals("INFO", req.priority());
    }

    @Test
    void testResolutionExpiringIn3MonthsTriggersWarningAlert() {
        ThirdPartyEntity thirdParty = new ThirdPartyEntity();
        thirdParty.setFullName("Clínica Imbanaco");

        ResolutionEntity resolution = new ResolutionEntity();
        resolution.setId(50L);
        resolution.setCode("RES-2024-001");
        resolution.setThirdParty(thirdParty);
        resolution.setExpirationDate(today.plusMonths(3));

        when(prescriptionInventoryRepository.findAllActiveStock()).thenReturn(List.of());
        when(resolutionRepository.findByIsActiveTrue()).thenReturn(List.of(resolution));
        when(notificationRepository.existsSimilarNotification(eq("CONTRACT"), any(), any(OffsetDateTime.class)))
                .thenReturn(false);

        int alerts = expirationAlertService.checkAndTriggerExpirationAlerts(false);

        assertEquals(1, alerts);
        ArgumentCaptor<CreateNotificationRequestDTO> captor = ArgumentCaptor.forClass(CreateNotificationRequestDTO.class);
        verify(notificationService, times(1)).createNotification(captor.capture());

        CreateNotificationRequestDTO req = captor.getValue();
        assertTrue(req.title().contains("⚠️ Resolución Próxima a Vencer (3 Meses)"));
        assertEquals("WARNING", req.priority());
        assertEquals("CONTRACT", req.category());
        assertTrue(req.message().contains("Clínica Imbanaco"));
    }

    @Test
    void testAntiDuplicatePreventsNotificationIfAlreadySentRecently() {
        ProductEntity product = new ProductEntity();
        product.setName("Morfina 10mg");

        PrescriptionInventoryEntity inventory = new PrescriptionInventoryEntity();
        inventory.setProduct(product);
        inventory.setExpirationDate(today);

        when(prescriptionInventoryRepository.findAllActiveStock()).thenReturn(List.of(inventory));
        when(resolutionRepository.findByIsActiveTrue()).thenReturn(List.of());
        // Simular que ya se emitió en las últimas 20 horas
        when(notificationRepository.existsSimilarNotification(eq("EXPIRATION_MEDICINE"), any(), any(OffsetDateTime.class)))
                .thenReturn(true);

        int alerts = expirationAlertService.checkAndTriggerExpirationAlerts(false);

        assertEquals(0, alerts);
        verify(notificationService, never()).createNotification(any());
    }
}
