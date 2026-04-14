package com.aurealab.service;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.model.aurea.entity.SoftwareValidationEntity;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface SoftwareValidationService {
    public ResponseEntity<APIResponseDTO<Set<SoftwareValidationEntity>>> getSoftwareValidation();
}
