package com.aurealab.service.impl.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.dto.CashRegister.request.ThirdPartyRequestDTO;
import com.aurealab.dto.ConfigParamDTO;
import com.aurealab.dto.CustomerTableDTO;
import com.aurealab.dto.response.ThirdPartyWithParamsResponseDTO;
import com.aurealab.mapper.CashRegister.ThirdPartyMapper;
import com.aurealab.model.cashRegister.entity.ThirdPartyEntity;
import com.aurealab.model.cashRegister.repository.ThirdPartyRepository;
import com.aurealab.service.CashRegister.ChargeService;
import com.aurealab.service.CashRegister.ThirdPartyRoleService;
import com.aurealab.service.CashRegister.ThirdPartyService;
import com.aurealab.service.ConfigParamService;
import com.aurealab.service.CustomerService;
import com.aurealab.service.impl.shared.TenantService;
import com.aurealab.util.JwtUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Autowired
    ChargeService chargeService;

    @Autowired
    ConfigParamService configParamsService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    ThirdPartyRoleService thirdPartyRoleService;

    private final String tenancy = "conduvalle";

    public ResponseEntity<APIResponseDTO<Set<ThirdPartyDTO>>> findCustomersByDocumentNumber(String documentNumber){
        return ResponseEntity.ok(APIResponseDTO.success(findByDniNumberContaining(documentNumber), constants.success.findedSuccess));
    }

    public Set<ThirdPartyDTO> findByDniNumberContaining(String documentNumber){
        Set<ThirdPartyDTO> response = new HashSet<>();
        Set<ThirdPartyEntity> thirdPartyEntities = tenantService.executeInTenant(tenancy, () -> thirdPartyRepository.findByDniNumberContaining(documentNumber));

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

    public ResponseEntity<APIResponseDTO<ThirdPartyDTO>> saveCustomer(ThirdPartyRequestDTO thirdPartyDTO){
        ThirdPartyDTO thirdParty = ThirdPartyDTO.builder()
                .id(thirdPartyDTO.id() == null ? null :thirdPartyDTO.id() )
                .documentType(thirdPartyDTO.documentType())
                .documentNumber(thirdPartyDTO.documentNumber())
                .fullName(thirdPartyDTO.fullName())
                .email(thirdPartyDTO.email())
                .address(thirdPartyDTO.address())
                .phoneNumber(thirdPartyDTO.phoneNumber())
                .createdBySystemUserId(jwtUtils.getCurrentUserId())
                .roles(thirdPartyRoleService.findAllEntitiesByIds(thirdPartyDTO.rolesIds()))
                .build();

        return ResponseEntity.ok(APIResponseDTO.success(saveThirdParty(thirdParty), constants.success.savedSuccess));
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

    public ResponseEntity<APIResponseDTO<ThirdPartyWithParamsResponseDTO>> findThirdPartyAndParamsById(Long id) {

        @SuppressWarnings("unchecked")
        ThirdPartyWithParamsResponseDTO params = configParamsService.findCreatingThirdPartyParams();

        return ResponseEntity.ok(APIResponseDTO.success(
                ThirdPartyWithParamsResponseDTO.builder()
                    .thirdParty(findThirdPartyById(id))
                    .documentTypes(params.documentTypes())
                    .roles(params.roles())
                    .charge(chargeService.findPendingChargeByThirdParty(id))
                    .build(), constants.success.findedSuccess));
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

    public ResponseEntity<APIResponseDTO<String>> findAllThirdParties(int page, int size, String searchValue) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ThirdPartyDTO> response = findAll(pageable);

        return ResponseEntity.ok(
                APIResponseDTO.withPageable("ok", constants.messages.consultGood, response)
        );
    }

    public Page<ThirdPartyDTO> findAll(Pageable pageable) {
        return tenantService.executeInTenant(tenancy, () -> {
            Page<ThirdPartyEntity> thirdPartyEntities = thirdPartyRepository.findAll(pageable);
            return thirdPartyEntities.map(ThirdPartyMapper::toDto);
        });
    }

}
