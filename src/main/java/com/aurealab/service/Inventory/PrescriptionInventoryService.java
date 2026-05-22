package com.aurealab.service.Inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.PrescriptionInventoryDTO;
import com.aurealab.model.inventory.entity.PrescriptionInventoryEntity;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface PrescriptionInventoryService {
    public ResponseEntity<APIResponseDTO<String>> getPrescriptionInventory(int page, int size, String searchValue, String type);
    public ResponseEntity<APIResponseDTO<String>> getExpiredPrescriptionInventory(int page, int size, String searchValue, String type);
    public ResponseEntity<APIResponseDTO<PrescriptionInventoryDTO>> getPrescriptionInventoryById(Long id);
    public PrescriptionInventoryEntity processPrescriptionInventory(PrescriptionInventoryDTO dto);
    public Set<PrescriptionInventoryDTO> getResolutionProductById(Long thirdPartyId);
    public PrescriptionInventoryEntity findByIdEntity(Long id);
    public ResponseEntity<APIResponseDTO<PrescriptionInventoryDTO>> drawalPresciptionInventory(Long id);
}
