package com.aurealab.service.impl;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.model.aurea.entity.SoftwareValidationEntity;
import com.aurealab.model.aurea.repository.SoftwareValidationRepository;
import com.aurealab.service.SoftwareValidationService;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SoftwareValidationServiceImpl implements SoftwareValidationService {

    @Autowired
    SoftwareValidationRepository softwareValidationRepository;

    public ResponseEntity<APIResponseDTO<Set<SoftwareValidationEntity>>> getSoftwareValidation(){

        Set<SoftwareValidationEntity> listSoftwareValidation = softwareValidationRepository.findAll();
        return ResponseEntity.ok(APIResponseDTO.success(listSoftwareValidation, constants.success.findedSuccess));
    }

}
