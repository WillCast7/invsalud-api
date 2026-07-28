package com.aurealab.service.impl;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.AuditLogDTO;
import com.aurealab.mapper.AuditLogMapper;
import com.aurealab.model.aurea.entity.AuditLogEntity;
import com.aurealab.model.aurea.repository.AuditLogRepository;
import com.aurealab.model.specs.AuditLogSpecs;
import com.aurealab.service.AuditLogService;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public ResponseEntity<APIResponseDTO<String>> findAllAuditLogs(int page, int size, String searchValue) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<AuditLogDTO> response = findAll(pageable, searchValue);

        return ResponseEntity.ok(
                APIResponseDTO.withPageable("ok", constants.messages.consultGood, response)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<APIResponseDTO<AuditLogDTO>> findAuditLogById(Long id) {
        AuditLogDTO response = findById(id);
        if (response == null) {
            throw new RuntimeException(constants.messages.dontFoundByID);
        }
        return ResponseEntity.ok(APIResponseDTO.success(response, constants.success.findedSuccess));
    }

    @Override
    public Page<AuditLogDTO> findAll(Pageable pageable, String searchValue) {
        Specification<AuditLogEntity> spec = AuditLogSpecs.search(searchValue);
        Page<AuditLogEntity> auditLogEntities = auditLogRepository.findAll(spec, pageable);
        return auditLogEntities.map(AuditLogMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogDTO findById(Long id) {
        Optional<AuditLogEntity> auditLog = auditLogRepository.findById(id);
        return auditLog.map(AuditLogMapper::toDTO).orElse(null);
    }
}
