package com.aurealab.service.Inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.request.PurchasingRequestDTO;
import com.aurealab.dto.PurchasingDTO;
import com.aurealab.dto.PrescriptionInventoryTableDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface PurchasingService {
    public ResponseEntity<APIResponseDTO<String>> getPurchasing(int page, int size, String searchValue, String type);
    public ResponseEntity<APIResponseDTO<PurchasingDTO>> getPurchasingById(Long id);
    public ResponseEntity<APIResponseDTO<String>> savePurchasing(PurchasingRequestDTO purchasingDTO);
    public Page<PrescriptionInventoryTableDTO> getPurchasingReport(
            int page, int size, String type,
            LocalDateTime start, LocalDateTime end,
            String documentNumber, String product, String batch);
}
