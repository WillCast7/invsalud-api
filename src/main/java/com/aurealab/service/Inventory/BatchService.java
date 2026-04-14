package com.aurealab.service.Inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.BatchDTO;
import org.springframework.http.ResponseEntity;

public interface BatchService {
    public ResponseEntity<APIResponseDTO<String>> findPaginatedBatches(int page, int size, String searchValue);
    public ResponseEntity<APIResponseDTO<BatchDTO>> changeStatus(Long id);
    public ResponseEntity<APIResponseDTO<BatchDTO>> saveBatch(BatchDTO batch);
}
