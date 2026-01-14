package com.aurealab.service.impl.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.model.aurea.repository.UserRepository;
import com.aurealab.model.cashRegister.entity.CashSessionEntity;
import com.aurealab.model.cashRegister.repository.CashSessionRepository;
import com.aurealab.service.CashRegister.CashRegisterService;
import com.aurealab.service.impl.shared.TenantService;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CashRegisterServiceImpl implements CashRegisterService {

    @Autowired
    CashSessionRepository cashSessionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private TenantService tenantService;

    public String GetAllCashSessions(){
        Iterable<CashSessionEntity> cashSession = cashSessionRepository.findAll();
        System.out.println("cashSession");
        System.out.println(cashSession);
        return "Omg";
    }

    public ResponseEntity<APIResponseDTO<List<CashSessionEntity>>> getAllSessions() {
        String tenant = "conduvalle";
        List<CashSessionEntity> cashSessionEntities;
        try{
            cashSessionEntities = tenantService.executeInTenant(tenant, () -> {
                return cashSessionRepository.findAll();
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(APIResponseDTO.success(cashSessionEntities, constants.messages.consultGood));
    }


}
