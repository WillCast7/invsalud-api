package com.aurealab.service.impl.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.model.cashRegister.entity.CashMovementItemsEntity;
import com.aurealab.model.cashRegister.repository.CashMovementProductsRepository;
import com.aurealab.service.CashRegister.CashMovementItemService;
import com.aurealab.service.impl.shared.TenantService;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class CashMovementItemServiceImpl implements CashMovementItemService {

    @Autowired
    private CashMovementProductsRepository cashMovementProductsRepository;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public ResponseEntity<APIResponseDTO<String>> updateStatus(Long id, String status) {
        List<String> validStatuses = Arrays.asList("PENDIENTE", "EN PROCESO", "TERMINADO");

        if (!validStatuses.contains(status.toUpperCase())) {
            return ResponseEntity.badRequest().body(APIResponseDTO.failure("Estado no válido. Los estados permitidos son: PENDIENTE, EN PROCESO, TERMINADO", constants.errors.unespectedError));
        }

        return tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            CashMovementItemsEntity entity = cashMovementProductsRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException(constants.messages.dontFoundByID));

            entity.setStatus(status.toUpperCase());

            if ("TERMINADO".equalsIgnoreCase(status)) {
                entity.setStatusDoneAt(LocalDateTime.now());
            }

            cashMovementProductsRepository.save(entity);

            return ResponseEntity.ok(APIResponseDTO.success(null, constants.messages.responseUpdateGood));
        });
    }
}
