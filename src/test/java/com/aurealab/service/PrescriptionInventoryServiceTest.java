package com.aurealab.service;

import com.aurealab.model.aurea.entity.CompanyEntity;
import com.aurealab.model.aurea.repository.CompanyRepository;
import com.aurealab.model.inventory.entity.PrescriptionInventoryEntity;
import com.aurealab.model.inventory.repository.PrescriptionInventoryRepository;
import com.aurealab.service.impl.inventory.PrescriptionInventoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrescriptionInventoryServiceTest {

    @Mock
    private PrescriptionInventoryRepository prescriptionInventoryRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private PrescriptionInventoryServiceImpl prescriptionInventoryService;

    @Test
    void testGetResolutionProductEntityByIdAppliesDaysLimitResolution() {
        Long thirdPartyId = 123L;
        int daysLimit = 15;

        CompanyEntity company = new CompanyEntity();
        company.setId(1L);
        company.setDaysLimitResolution(daysLimit);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(prescriptionInventoryRepository.findByThirdPartyIdGranted(eq(thirdPartyId), any(LocalDate.class)))
                .thenReturn(Set.of(new PrescriptionInventoryEntity()));

        Set<PrescriptionInventoryEntity> result = prescriptionInventoryService.getResolutionProductEntityById(thirdPartyId);

        assertEquals(1, result.size());

        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(prescriptionInventoryRepository).findByThirdPartyIdGranted(eq(thirdPartyId), dateCaptor.capture());

        LocalDate expectedMinDate = LocalDate.now().plusDays(daysLimit);
        assertEquals(expectedMinDate, dateCaptor.getValue());
    }

    @Test
    void testGetResolutionProductEntityByIdDefaultsToZeroDaysIfNoCompany() {
        Long thirdPartyId = 456L;

        when(companyRepository.findById(1L)).thenReturn(Optional.empty());
        when(prescriptionInventoryRepository.findByThirdPartyIdGranted(eq(thirdPartyId), any(LocalDate.class)))
                .thenReturn(Set.of());

        prescriptionInventoryService.getResolutionProductEntityById(thirdPartyId);

        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(prescriptionInventoryRepository).findByThirdPartyIdGranted(eq(thirdPartyId), dateCaptor.capture());

        assertEquals(LocalDate.now(), dateCaptor.getValue());
    }
}
