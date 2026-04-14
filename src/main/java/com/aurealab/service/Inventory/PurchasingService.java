package com.aurealab.service.Inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.request.PurchasingRequestDTO;
import com.aurealab.dto.PurchasingDTO;
import org.springframework.http.ResponseEntity;

public interface PurchasingService {
    public ResponseEntity<APIResponseDTO<String>> getPurchasing(int page, int size, String searchValue, String type);
    public ResponseEntity<APIResponseDTO<PurchasingDTO>> getPurchasingById(Long id);
    public ResponseEntity<APIResponseDTO<String>> savePurchasing(PurchasingRequestDTO purchasingDTO);
}
