package com.aurealab.service.impl.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.CashMovementDTO;
import com.aurealab.mapper.CashMovementMapper;
import com.aurealab.model.cashRegister.entity.CashMovementEntity;
import com.aurealab.model.cashRegister.repository.CashMovementRepository;
import com.aurealab.service.CashRegister.CashMovementService;
import com.aurealab.service.impl.shared.TenantService;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CashMovementServiceImpl implements CashMovementService {


    @Autowired
    CashMovementRepository cashMovementRepository;

    @Autowired
    private TenantService tenantService;

    public ResponseEntity<APIResponseDTO<Set<CashMovementDTO>>> getAllTransactions() {
        String tenant = "conduvalle";
        List<CashMovementEntity> cashSessionEntities;
        try{
            cashSessionEntities = tenantService.executeInTenant(tenant, () -> {
                return cashMovementRepository.findAll();
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Set<CashMovementDTO> response= new HashSet<>();
        cashSessionEntities
                .forEach(cashSessionEntity -> response.add(CashMovementMapper.toDto(cashSessionEntity)));
        return ResponseEntity.ok(APIResponseDTO.success(response, constants.messages.consultGood));
    }
}
