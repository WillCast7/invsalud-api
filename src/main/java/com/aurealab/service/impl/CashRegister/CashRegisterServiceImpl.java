package com.aurealab.service.impl.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.CashSessionDTO;
import com.aurealab.dto.CashRegister.response.CashSessionsResponseDTO;
import com.aurealab.dto.tables.CashMovementTableDTO;
import com.aurealab.mapper.CashRegister.CashMovementMapper;
import com.aurealab.mapper.CashRegister.CashSessionMapper;
import com.aurealab.model.aurea.repository.UserRepository;
import com.aurealab.model.cashRegister.entity.CashMovementEntity;
import com.aurealab.model.cashRegister.entity.CashSessionEntity;
import com.aurealab.model.cashRegister.repository.CashSessionRepository;
import com.aurealab.model.cashRegister.specs.CashMovementSpecs;
import com.aurealab.model.cashRegister.specs.CashSessionSpecs;
import com.aurealab.service.CashRegister.CashRegisterService;
import com.aurealab.service.impl.shared.TenantService;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CashRegisterServiceImpl implements CashRegisterService {

    @Autowired
    CashSessionRepository cashSessionRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private TenantService tenantService;

    public ResponseEntity<APIResponseDTO<String>> GetAllCashSessions(int page, int size, String searchValue){

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<CashSessionDTO> cashSession = findPageable(searchValue, pageable);

        return ResponseEntity.ok(
                APIResponseDTO.withPageable("ok", constants.messages.consultGood, cashSession)
        );
    }

    public ResponseEntity<APIResponseDTO<List<CashSessionEntity>>> getAllSessions() {
;
        List<CashSessionEntity> cashSessionEntities;
        try{
            cashSessionEntities = tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
                return cashSessionRepository.findAll();
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(APIResponseDTO.success(cashSessionEntities, constants.messages.consultGood));
    }

    public Page<CashSessionDTO> findPageable(String searchValue, Pageable pageable){
        Specification<CashSessionEntity> spec = CashSessionSpecs.searchByMovementOrThirdParty(searchValue);

        return tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            Page<CashSessionEntity> entities = cashSessionRepository.findAll(spec, pageable);
            return entities.map(CashSessionMapper::toDto);
        });
    }

}
