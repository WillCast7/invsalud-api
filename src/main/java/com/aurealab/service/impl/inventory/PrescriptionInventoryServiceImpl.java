package com.aurealab.service.impl.inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.PrescriptionInventoryDTO;
import com.aurealab.dto.PrescriptionInventoryTableDTO;
import com.aurealab.mapper.inventory.PrescriptionInventoryMapper;
import com.aurealab.model.inventory.entity.PrescriptionInventoryEntity;
import com.aurealab.model.inventory.repository.PrescriptionInventoryRepository;
import com.aurealab.model.specs.PrescriptionInventorySpecs;
import com.aurealab.service.Inventory.PrescriptionInventoryService;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
public class PrescriptionInventoryServiceImpl implements PrescriptionInventoryService {

    @Autowired
    PrescriptionInventoryRepository prescriptionInventoryRepository;

    public ResponseEntity<APIResponseDTO<String>> getPrescriptionInventory(int page, int size, String searchValue) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(APIResponseDTO.withPageable(constants.success.findedSuccess, constants.success.findedSuccess, findAllToTable(pageable, searchValue)));
    }

    public ResponseEntity<APIResponseDTO<PrescriptionInventoryDTO>> getPrescriptionInventoryById(Long id){
            PrescriptionInventoryDTO response = findById(id);
            if(response == null) throw new RuntimeException(constants.messages.noData);
            return ResponseEntity.ok(APIResponseDTO.success(response, constants.success.findedSuccess));
    }

    public Page<PrescriptionInventoryDTO> findAll(Pageable pageable, String searchValue) {
        Specification<PrescriptionInventoryEntity> spec = PrescriptionInventorySpecs.search(searchValue);
        Page<PrescriptionInventoryEntity> prescriptionInventoryEntities = prescriptionInventoryRepository.findAll(spec, pageable);
        return prescriptionInventoryEntities.map(PrescriptionInventoryMapper::toDto);
    }

    public Page<PrescriptionInventoryTableDTO> findAllToTable(Pageable pageable, String searchValue) {
        Specification<PrescriptionInventoryEntity> spec = PrescriptionInventorySpecs.search(searchValue);
        Page<PrescriptionInventoryEntity> prescriptionInventoryEntities = prescriptionInventoryRepository.findAll(spec, pageable);
        return prescriptionInventoryEntities.map(PrescriptionInventoryMapper::toTableDto);
    }

    public PrescriptionInventoryDTO findById(Long id) {
        Optional<PrescriptionInventoryEntity> prescriptionInventory = prescriptionInventoryRepository.findById(id);
        return prescriptionInventory.map(PrescriptionInventoryMapper::toDto).orElse(null);
    }

    @Transactional
    public PrescriptionInventoryEntity processPrescriptionInventory(PrescriptionInventoryDTO dto) {
        Optional<PrescriptionInventoryEntity> existing = prescriptionInventoryRepository.findByBatchIdAndProductIdAndExpirationDate(
                dto.batch().id(), dto.product().id(), dto.expirationDate()
        );

        System.out.println("processing prescription inventory");
        System.out.println(":)");


        if (existing.isPresent()) {
            System.out.println("Is present");

            PrescriptionInventoryEntity entity = existing.get();
            entity.setTotalUnits(entity.getTotalUnits() + dto.totalUnits());
            entity.setAvailableUnits(entity.getAvailableUnits() + dto.availableUnits());
            return prescriptionInventoryRepository.save(entity);
        } else {
            System.out.println("Prescription inventory not found");
            PrescriptionInventoryEntity newEntity = PrescriptionInventoryMapper.toEntity(dto);
            return prescriptionInventoryRepository.save(newEntity);
        }
    }
}
