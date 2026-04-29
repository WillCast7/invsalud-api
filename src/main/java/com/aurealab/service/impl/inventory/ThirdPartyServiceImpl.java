package com.aurealab.service.impl.inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.dto.CashRegister.request.ThirdPartyRequestDTO;
import com.aurealab.dto.response.ThirdPartyWithParamsResponseDTO;
import com.aurealab.mapper.inventory.ThirdPartyMapper;
import com.aurealab.model.inventory.entity.ThirdPartyEntity;
import com.aurealab.model.inventory.repository.ThirdPartyRepository;
import com.aurealab.service.Inventory.ThirdPartyRoleService;
import com.aurealab.service.Inventory.ThirdPartyService;
import com.aurealab.service.ConfigParamService;
import com.aurealab.util.JwtUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    ConfigParamService configParamsService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    ThirdPartyRoleService thirdPartyRoleService;


    @Transactional
    public ResponseEntity<APIResponseDTO<Set<ThirdPartyDTO>>> findCustomersByDocumentNumber(String documentNumber){
        return ResponseEntity.ok(APIResponseDTO.success(findByDniNumberContaining(documentNumber), constants.success.findedSuccess));
    }

    public Set<ThirdPartyDTO> findByDniNumberContaining(String documentNumber){
        Set<ThirdPartyDTO> response = new HashSet<>();
        Set<ThirdPartyEntity> thirdPartyEntities = thirdPartyRepository.findByDniNumberContaining(documentNumber);

        thirdPartyEntities.forEach(thirdPartyEntity -> response.add(ThirdPartyMapper.toDto(thirdPartyEntity)));

        return response;
    }

    @Transactional
    public ThirdPartyDTO findByDniNumberAndDniType(String documentNumber, String docuentType){

        ThirdPartyEntity thirdPartyEntity =  thirdPartyRepository.findByDniNumberAndDniType(docuentType, documentNumber);


        return thirdPartyEntity != null ? ThirdPartyMapper.toDto(thirdPartyEntity) : null;

    }

    @Transactional
    public List<ThirdPartyDTO> findThirdPartyByRole(String role){
        List<ThirdPartyDTO> response = new ArrayList<>();
        Set<ThirdPartyEntity> thirdPartyEntities =  thirdPartyRepository.findAllWithRoleByRole(role);

        thirdPartyEntities.forEach(thirdPartyEntity -> response.add(ThirdPartyMapper.toDto(thirdPartyEntity)));

        return response;
    }

    @Transactional
    public ResponseEntity<APIResponseDTO<ThirdPartyDTO>> saveCustomer(ThirdPartyRequestDTO thirdPartyDTO){
        ThirdPartyDTO thirdParty = ThirdPartyDTO.builder()
                .id(thirdPartyDTO.id() == null ? null :thirdPartyDTO.id() )
                .documentType(thirdPartyDTO.documentType())
                .documentNumber(thirdPartyDTO.documentNumber())
                .fullName(thirdPartyDTO.fullName())
                .email(thirdPartyDTO.email())
                .address(thirdPartyDTO.address())
                .phoneNumber(thirdPartyDTO.phoneNumber())
                .createdBy(jwtUtils.getCurrentUserId())
                .roles(thirdPartyRoleService.findAllEntitiesByIds(thirdPartyDTO.rolesIds()))
                .resolutions(thirdPartyDTO.resolutions())
                .build();

        return ResponseEntity.ok(APIResponseDTO.success(saveThirdParty(thirdParty), constants.success.savedSuccess));
    }

    @Transactional
    public ThirdPartyDTO saveThirdParty(ThirdPartyDTO thirdParty){
        return ThirdPartyMapper.toDto(
                thirdPartyRepository.save(
                        ThirdPartyMapper.toEntity(thirdParty)
                )
        );
    }

    @Transactional
    public ResponseEntity<APIResponseDTO<ThirdPartyWithParamsResponseDTO>> findThirdPartyAndParamsById(Long id) {

        @SuppressWarnings("unchecked")
        ThirdPartyWithParamsResponseDTO params = configParamsService.findCreatingThirdPartyParams();

        return ResponseEntity.ok(APIResponseDTO.success(
                ThirdPartyWithParamsResponseDTO.builder()
                    .thirdParty(findThirdPartyById(id))
                    .documentTypes(params.documentTypes())
                    .roles(params.roles())
                    .build(), constants.success.findedSuccess));
    }

    @Transactional
    public ThirdPartyDTO findThirdPartyById(Long id) {
        return  ThirdPartyMapper.toDto(findThirdPartyEntityById(id));
    }

    public ThirdPartyEntity findThirdPartyEntityById(Long id) {
        return thirdPartyRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(constants.messages.dontFoundByID)
        );
    }

    public ResponseEntity<APIResponseDTO<String>> findAllThirdParties(int page, int size, String searchValue) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ThirdPartyDTO> response = findAll(pageable, searchValue);

        return ResponseEntity.ok(
                APIResponseDTO.withPageable("ok", constants.messages.consultGood, response)
        );
    }

    public Page<ThirdPartyDTO> findAll(Pageable pageable, String searchValue) {
        Page<ThirdPartyEntity> thirdPartyEntities = thirdPartyRepository.findAll(pageable);
        return thirdPartyEntities.map(ThirdPartyMapper::toDtoList);
    }

}
