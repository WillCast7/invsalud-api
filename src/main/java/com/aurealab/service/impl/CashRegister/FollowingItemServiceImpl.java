package com.aurealab.service.impl.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.model.cashRegister.entity.FollowingItemEntity;
import com.aurealab.model.cashRegister.repository.FollowingItemRepository;
import com.aurealab.service.CashRegister.FollowingItemService;
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
public class FollowingItemServiceImpl implements FollowingItemService {

    @Autowired
    private FollowingItemRepository followingItemRepository;

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
            FollowingItemEntity entity = followingItemRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException(constants.messages.dontFoundByID));

            entity.setStatus(status.toUpperCase());

            if ("TERMINADO".equalsIgnoreCase(status)) {
                entity.setStatusDoneAt(LocalDateTime.now());
            }
            try{
                followingItemRepository.save(entity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return ResponseEntity.ok(APIResponseDTO.success(null, constants.messages.responseUpdateGood));
        });
    }
}
