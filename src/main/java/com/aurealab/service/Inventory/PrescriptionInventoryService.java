package com.aurealab.service.Inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.PrescriptionInventoryDTO;
import com.aurealab.model.inventory.entity.PrescriptionInventoryEntity;
import org.springframework.http.ResponseEntity;

public interface PrescriptionInventoryService {
    public ResponseEntity<APIResponseDTO<String>> getPrescriptionInventory(int page, int size, String searchValue);
    public ResponseEntity<APIResponseDTO<PrescriptionInventoryDTO>> getPrescriptionInventoryById(Long id);
    public PrescriptionInventoryEntity processPrescriptionInventory(PrescriptionInventoryDTO dto);
}
