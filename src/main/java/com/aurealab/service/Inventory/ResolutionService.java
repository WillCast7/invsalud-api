package com.aurealab.service.Inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.ResolutionDTO;
import org.springframework.http.ResponseEntity;

public interface ResolutionService {
    public ResponseEntity<APIResponseDTO<String>> getResolutions(int page, int size, String searchValue);
    public ResponseEntity<APIResponseDTO<ResolutionDTO>> getResolutionById(Long id);
}
