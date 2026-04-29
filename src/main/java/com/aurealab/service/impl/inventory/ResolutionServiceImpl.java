package com.aurealab.service.impl.inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.OrderDTO;
import com.aurealab.dto.ResolutionDTO;
import com.aurealab.dto.tables.OrderTableDTO;
import com.aurealab.dto.tables.ResolutionTableDTO;
import com.aurealab.mapper.inventory.OrderMapper;
import com.aurealab.mapper.inventory.ResolutionMapper;
import com.aurealab.model.inventory.entity.OrderEntity;
import com.aurealab.model.inventory.entity.ResolutionEntity;
import com.aurealab.model.inventory.repository.ResolutionRepository;
import com.aurealab.model.specs.OrderSpecs;
import com.aurealab.model.specs.ResolutionSpecs;
import com.aurealab.service.Inventory.ResolutionService;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ResolutionServiceImpl implements ResolutionService {

    @Autowired
    ResolutionRepository resolutionRepository;

    public ResponseEntity<APIResponseDTO<String>> getResolutions(int page, int size, String searchValue) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(APIResponseDTO.withPageable(constants.success.findedSuccess, constants.success.findedSuccess, findAllToTable(pageable, searchValue)));
    }

    @Transactional
    public ResponseEntity<APIResponseDTO<ResolutionDTO>> getResolutionById(Long id){
        ResolutionDTO response = findById(id);
        if(response == null) throw new RuntimeException(constants.messages.noData);

        return ResponseEntity.ok(APIResponseDTO.success(response, constants.success.findedSuccess));
    }

    public Page<ResolutionDTO> findAll(Pageable pageable, String searchValue) {
        Specification<ResolutionEntity> spec = ResolutionSpecs.search(searchValue);
        Page<ResolutionEntity> prescriptionInventoryEntities = resolutionRepository.findAll(spec, pageable);
        return prescriptionInventoryEntities.map(ResolutionMapper::toDto);
    }

    public Page<ResolutionTableDTO> findAllToTable(Pageable pageable, String searchValue) {
        Specification<ResolutionEntity> spec = ResolutionSpecs.search(searchValue);
        Page<ResolutionEntity> prescriptionInventoryEntities = resolutionRepository.findAll(spec, pageable);
        return prescriptionInventoryEntities.map(ResolutionMapper::toTableDto);
    }

    @Transactional(readOnly = true)
    public ResolutionDTO findById(Long id) {
        Optional<ResolutionEntity> prescriptionInventory = resolutionRepository.findById(id);
        System.out.println("prescriptionInventory: ");
        System.out.println("prescriptionInventory: " + prescriptionInventory.get().getCode());
        return prescriptionInventory.map(ResolutionMapper::toDto).orElse(null);
    }
}
