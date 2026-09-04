package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CompanyDTO;
import com.aurealab.service.CompanyService;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    CompanyService companyService;

    @GetMapping
    public ResponseEntity<APIResponseDTO<CompanyDTO>> getCompany() {
        return ResponseEntity.ok(APIResponseDTO.success(companyService.getCompany(), constants.success.findedSuccess));
    }

    @org.springframework.web.bind.annotation.PutMapping
    public ResponseEntity<APIResponseDTO<CompanyDTO>> updateCompany(@org.springframework.web.bind.annotation.RequestBody CompanyDTO companyDTO) {
        return ResponseEntity.ok(APIResponseDTO.success(companyService.updateCompany(companyDTO), constants.success.updatedSuccess));
    }

    @org.springframework.web.bind.annotation.PatchMapping
    public ResponseEntity<APIResponseDTO<CompanyDTO>> patchCompany(@org.springframework.web.bind.annotation.RequestBody CompanyDTO companyDTO) {
        return ResponseEntity.ok(APIResponseDTO.success(companyService.updateCompany(companyDTO), constants.success.updatedSuccess));
    }

}
