package com.aurealab.service.impl.inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.PrescriptionInventoryDTO;
import com.aurealab.dto.PrescriptionInventoryTableDTO;
import com.aurealab.mapper.inventory.PrescriptionInventoryMapper;
import com.aurealab.model.inventory.entity.PrescriptionInventoryEntity;
import com.aurealab.model.inventory.entity.ProductEntity;
import com.aurealab.model.inventory.repository.PrescriptionInventoryRepository;
import com.aurealab.model.specs.PrescriptionInventorySpecs;
import com.aurealab.service.Inventory.PrescriptionInventoryService;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import com.aurealab.util.exceptions.BaseException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class PrescriptionInventoryServiceImpl implements PrescriptionInventoryService {

    @Autowired
    PrescriptionInventoryRepository prescriptionInventoryRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    DocumentSequenceServiceImpl documentSequenceService;


    @Transactional
    public ResponseEntity<APIResponseDTO<String>> getPrescriptionInventory(int page, int size, String searchValue, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(APIResponseDTO.withPageable(constants.success.findedSuccess, constants.success.findedSuccess, findAllToTable(pageable, searchValue, type)));
    }

    @Transactional
    public ResponseEntity<APIResponseDTO<String>> getExpiredPrescriptionInventory(int page, int size, String searchValue, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(APIResponseDTO.withPageable(constants.success.findedSuccess, constants.success.findedSuccess, findAllToTable(pageable, searchValue, type)));
    }

    public ResponseEntity<APIResponseDTO<PrescriptionInventoryDTO>> getPrescriptionInventoryById(Long id){
            PrescriptionInventoryDTO response = findById(id);
            if(response == null) throw new RuntimeException(constants.messages.noData);
            return ResponseEntity.ok(APIResponseDTO.success(response, constants.success.findedSuccess));
    }

    public Page<PrescriptionInventoryDTO> findAll(Pageable pageable, String searchValue) {
        Specification<PrescriptionInventoryEntity> spec = PrescriptionInventorySpecs.search(searchValue, constants.productTypes.SpecialControl);
        Page<PrescriptionInventoryEntity> prescriptionInventoryEntities = prescriptionInventoryRepository.findAll(spec, pageable);
        return prescriptionInventoryEntities.map(PrescriptionInventoryMapper::toDto);
    }

    @Transactional
    public Page<PrescriptionInventoryTableDTO> findAllToTable(Pageable pageable, String searchValue, String type) {
        Specification<PrescriptionInventoryEntity> spec = Objects.equals(type, "expired") || Objects.equals(type, "removed") ?
                PrescriptionInventorySpecs.searchExpired(searchValue, type):
                PrescriptionInventorySpecs.search(searchValue, type);

        Page<PrescriptionInventoryEntity> prescriptionInventoryEntities =
                prescriptionInventoryRepository.findAll(spec, pageable);

        return prescriptionInventoryEntities.map(PrescriptionInventoryMapper::toTableDto);
    }

    @Transactional
    public ResponseEntity<APIResponseDTO<PrescriptionInventoryDTO>> drawalPresciptionInventory(Long id){

        PrescriptionInventoryEntity prescriptionInventory =  findByIdEntity(id);

        if(prescriptionInventory.isDrawal()) throw new RuntimeException(constants.messages.medicineDrawaled);

        prescriptionInventory.setDrawal(true);
        prescriptionInventory.setActive(false);
        prescriptionInventory.setWithdrawalBy(jwtUtils.getCurrentUserId());
        prescriptionInventory.setWithdrawnAt(LocalDateTime.now());
        prescriptionInventory.setWithdrawalCode(documentSequenceService.getNextInvoiceNumber(constants.configParam.drawalMedicine));
        return ResponseEntity.ok(
                APIResponseDTO.success(
                    PrescriptionInventoryMapper.toDto(
                        prescriptionInventoryRepository.save(
                                prescriptionInventory
                        )
                    ),
                    constants.success.updatedSuccess
                )
        );
    }

    public PrescriptionInventoryDTO findById(Long id) {
        return PrescriptionInventoryMapper.toDto(findByIdEntity(id));
    }

    public PrescriptionInventoryEntity findByIdEntity(Long id) {
        return prescriptionInventoryRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(constants.messages.dontFoundByID));
    }


    @Transactional
    public PrescriptionInventoryEntity processPrescriptionInventory(PrescriptionInventoryDTO dto) {
        Optional<PrescriptionInventoryEntity> existing = prescriptionInventoryRepository.findByBatchIdAndProductIdAndExpirationDate(
                dto.batch().id(), dto.product().id(), dto.expirationDate()
        );

        if (existing.isPresent()) {
            PrescriptionInventoryEntity entity = existing.get();
            entity.setTotalUnits(entity.getTotalUnits() + dto.totalUnits());
            entity.setAvailableUnits(entity.getAvailableUnits() + dto.availableUnits());
            return prescriptionInventoryRepository.save(entity);

        } else {
            PrescriptionInventoryEntity newEntity = PrescriptionInventoryMapper.toEntity(dto);
            return prescriptionInventoryRepository.save(newEntity);
        }
    }

    @Transactional
    public Set<PrescriptionInventoryDTO> getResolutionProductById(Long thirdPartyId){
        Set<PrescriptionInventoryDTO> prescriptionInventories = new HashSet<>();
        getResolutionProductEntityById(thirdPartyId).forEach(prescriptionInventory ->
                prescriptionInventories.add(PrescriptionInventoryMapper.toDto(prescriptionInventory))
                );
        System.out.println("getting resolution productsss");
        System.out.println(prescriptionInventories);
        return prescriptionInventories;
    }

    @Transactional
    public Set<PrescriptionInventoryEntity> getResolutionProductEntityById(Long thirdPartyId){
        return  prescriptionInventoryRepository.findByThirdPartyIdGranteed(thirdPartyId);
    }

}
