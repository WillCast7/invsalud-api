package com.aurealab.service.impl.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.CashSessionDTO;
import com.aurealab.dto.UserDTO;
import com.aurealab.mapper.CashRegister.CashSessionMapper;
import com.aurealab.model.cashRegister.entity.CashSessionEntity;
import com.aurealab.model.cashRegister.repository.CashSessionRepository;
import com.aurealab.service.CashRegister.CashSessionService;
import com.aurealab.service.UserService;
import com.aurealab.service.impl.shared.TenantService;
import com.aurealab.util.JwtUtils;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.aurealab.util.constants;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CashSessionServiceImpl implements CashSessionService {

    @Autowired
    CashSessionRepository cashSessionRepository;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    public ResponseEntity<APIResponseDTO<Set<CashSessionDTO>>> getAllSessions(){
        return ResponseEntity.ok(APIResponseDTO.success(findAll(), constants.messages.consultGood));
    }

    public Set<CashSessionDTO> findAll() {
        return cashSessionRepository.findAll()
                .stream()
                .map(CashSessionMapper::toDto)
                .collect(Collectors.toSet());
    }

    public Page<CashSessionDTO> findAll(Pageable pageable) {
        return cashSessionRepository.findAll(pageable)
                .map(CashSessionMapper::toDto);
    }

    public CashSessionDTO findById(Long id) {
        CashSessionEntity cashSessionEntity =
        tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            return cashSessionRepository.findById(id).get();
        });

        return CashSessionMapper.toDto(cashSessionEntity);
    }

    public CashSessionDTO findTodaySession() {
        CashSessionEntity cashSessionEntity =
        tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            return cashSessionRepository.findByBusinessDateAndStatus(
                    LocalDate.now(),
                    "OPEN"
            );
        });

        return CashSessionMapper.toDto(cashSessionEntity);
    }

    public CashSessionDTO findOpenedSession() {
        CashSessionEntity cashSessionEntity =
        tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            return cashSessionRepository.findByBusinessDateLessThanAndStatus(
                    LocalDate.now(),
                    "OPEN"
            );
        });

        return CashSessionMapper.toDto(cashSessionEntity);
    }

    public ResponseEntity<APIResponseDTO<CashSessionDTO>> initializeCashSession(CashSessionDTO cashSessionDTO){
        Long idLogged = jwtUtils.getCurrentUserId();
        UserDTO userDTO = userService.getUserById(jwtUtils.getCurrentUserId());
        if(findTodaySession()!= null){
            throw new RuntimeException(constants.messages.cashSessionExist);
        }

        if (idLogged == null) {
            throw new RuntimeException(constants.messages.asd);
        }

        CashSessionEntity cashSessionEntity = new CashSessionEntity();
        cashSessionEntity.setOpeningAmount(cashSessionDTO.openingAmount());
        cashSessionEntity.setOpenedBySystemUserId(idLogged);
        cashSessionEntity.setCreatedByUserName(
                userDTO.getPerson().getNames()
                        + " " +
                userDTO.getPerson().getSurnames()
        );
        cashSessionEntity.setStatus(constants.configParam.statusOpen);
        System.out.println("shit this here");
        System.out.println(userDTO.getPerson().getNames() + " " + userDTO.getPerson().getSurnames());
        CashSessionDTO result = createCashSession(cashSessionEntity);

        return ResponseEntity.ok(APIResponseDTO.success(result, constants.messages.consultGood));

    }

    public ResponseEntity<APIResponseDTO<CashSessionDTO>> finalizeCashSession(CashSessionDTO cashSessionDTO){

        return ResponseEntity.ok(APIResponseDTO.success(findAndUpdate(cashSessionDTO), constants.messages.consultGood));

    }

    public CashSessionDTO createCashSession(CashSessionEntity cashSession){
        return tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            return CashSessionMapper.toDto(
                    cashSessionRepository.save(cashSession)
            );
        });
    }

    public CashSessionDTO findAndUpdate(CashSessionDTO cashSessionDTO){

        return tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            CashSessionEntity cashSessionFinded = cashSessionRepository.findById(cashSessionDTO.id()).get();
            cashSessionFinded.setStatus(constants.configParam.statusClose);
            cashSessionFinded.setClosingAmount(cashSessionDTO.closingAmount());
            cashSessionFinded.setClosedAt(OffsetDateTime.now());
            cashSessionFinded.setClosedBySystemUserId(jwtUtils.getCurrentUserId());
            return createCashSession(cashSessionFinded);
        });
    }

}
