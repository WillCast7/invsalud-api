package com.aurealab.service.impl.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.mapper.CashRegister.ThirdPartyMapper;
import com.aurealab.model.cashRegister.entity.ThirdPartyEntity;
import com.aurealab.model.cashRegister.repository.ThirdPartyRepository;
import com.aurealab.service.CashRegister.ThirdPartyService;
import com.aurealab.service.impl.shared.TenantService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.aurealab.util.constants;

import java.util.*;

@Service
public class ThirdPartyServiceImpl implements ThirdPartyService {

    @Autowired
    ThirdPartyRepository thirdPartyRepository;

    @Autowired
    TenantService tenantService;

    private final String tenancy = "conduvalle";

    public ResponseEntity<APIResponseDTO<Set<ThirdPartyDTO>>> findCustomers(String documentNumber){

        return ResponseEntity.ok(APIResponseDTO.success(findByDniNumberContaining(documentNumber), constants.success.findedSuccess));
    }

    public Set<ThirdPartyDTO> findByDniNumberContaining(String documentNumber){
        Set<ThirdPartyDTO> response = new HashSet<>();
        Set<ThirdPartyEntity> thirdPartyEntities = tenantService.executeInTenant(tenancy, () -> {
            return thirdPartyRepository.findByDniNumberContaining(documentNumber);
        });

        thirdPartyEntities.forEach(thirdPartyEntity -> response.add(ThirdPartyMapper.toDto(thirdPartyEntity)));

        return response;
    }

    public ThirdPartyDTO findByDniNumberAndDniType(String documentNumber, String docuentType){

        ThirdPartyEntity thirdPartyEntity = tenantService.executeInTenant(tenancy, () -> {
            return thirdPartyRepository.findByDniNumberAndDniType(docuentType, documentNumber);
        });

        return thirdPartyEntity != null ? ThirdPartyMapper.toDto(thirdPartyEntity) : null;

    }

    public List<ThirdPartyDTO> findThirdPartyByRole(String role){
        List<ThirdPartyDTO> response = new ArrayList<>();
        Set<ThirdPartyEntity> thirdPartyEntities = tenantService.executeInTenant(tenancy, () -> {
            return thirdPartyRepository.findAllWithRoleByRole(role);
        });

        thirdPartyEntities.forEach(thirdPartyEntity -> response.add(ThirdPartyMapper.toDto(thirdPartyEntity)));

        return response;
    }

    public ThirdPartyDTO saveThirdParty(ThirdPartyDTO thirdParty){
        System.out.println("save");
        return tenantService.executeInTenant(tenancy, () -> {
            return ThirdPartyMapper.toDto(
                    thirdPartyRepository.save(
                            ThirdPartyMapper.toEntity(thirdParty)
                    )
            );
        });
    }

    public ThirdPartyDTO findThirdPartyById(Long id) {
        return tenantService.executeInTenant(tenancy, () ->
                thirdPartyRepository.findById(id)
                        .map(ThirdPartyMapper::toDto)
                        .orElseThrow(() ->
                                new EntityNotFoundException("ThirdParty no encontrado con id: " + id)
                        )
        );
    }
}
