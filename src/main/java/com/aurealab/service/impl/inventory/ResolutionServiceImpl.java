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

import com.aurealab.model.inventory.entity.ProductEntity;
import com.aurealab.model.inventory.entity.ResolutionAllowedProductEntity;
import com.aurealab.model.inventory.repository.ThirdPartyRepository;
import com.aurealab.service.Inventory.DocumentSequenceService;
import com.aurealab.util.JwtUtils;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

@Service
public class ResolutionServiceImpl implements ResolutionService {

    @Autowired
    ResolutionRepository resolutionRepository;

    @Autowired
    ThirdPartyRepository thirdPartyRepository;

    @Autowired
    DocumentSequenceService documentSequenceService;

    @Autowired
    private JwtUtils jwtUtils;

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
        return prescriptionInventory.map(ResolutionMapper::toDto).orElse(null);
    }

    @Transactional
    public ResponseEntity<APIResponseDTO<ResolutionDTO>> saveResolution(ResolutionDTO resolutionDTO) {
        ResolutionEntity entity = ResolutionMapper.toEntity(resolutionDTO);
        if (resolutionDTO.thirdParty() != null && resolutionDTO.thirdParty().id() != null) {
            entity.setThirdParty(thirdPartyRepository.findById(resolutionDTO.thirdParty().id())
                    .orElseThrow(() -> new RuntimeException("Tercero no encontrado")));
        }
        if (entity.getCode() == null || entity.getCode().trim().isEmpty()) {
            entity.setCode(documentSequenceService.getNextInvoiceNumber(constants.configParam.resolutionPrefix));
        }
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        if (entity.getCreatedBy() == null) {
            Long currentUserId = jwtUtils.getCurrentUserId();
            entity.setCreatedBy(currentUserId != null ? String.valueOf(currentUserId) : "1");
        }
        if (entity.getIsActive() == null) {
            entity.setIsActive(true);
        }
        ResolutionEntity saved = resolutionRepository.save(entity);
        return ResponseEntity.ok(APIResponseDTO.success(ResolutionMapper.toDto(saved), constants.success.savedSuccess));
    }

    @Transactional
    public ResponseEntity<APIResponseDTO<ResolutionDTO>> updateResolution(Long id, ResolutionDTO resolutionDTO) {
        ResolutionEntity existing = resolutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(constants.messages.noData));

        if (resolutionDTO.thirdParty() != null && resolutionDTO.thirdParty().id() != null) {
            existing.setThirdParty(thirdPartyRepository.findById(resolutionDTO.thirdParty().id()).orElse(existing.getThirdParty()));
        }
        if (resolutionDTO.startDate() != null) {
            existing.setStartDate(resolutionDTO.startDate());
        }
        if (resolutionDTO.expirationDate() != null) {
            existing.setExpirationDate(resolutionDTO.expirationDate());
        }
        existing.setDescription(resolutionDTO.description());
        if (resolutionDTO.isActive() != null) {
            existing.setIsActive(resolutionDTO.isActive());
        }

        if (resolutionDTO.products() != null) {
            if (existing.getAllowedProduct() != null) {
                existing.getAllowedProduct().clear();
            } else {
                existing.setAllowedProduct(new HashSet<>());
            }
            resolutionDTO.products().forEach(p -> {
                ResolutionAllowedProductEntity rap = new ResolutionAllowedProductEntity();
                rap.setResolution(existing);
                rap.setProduct(new ProductEntity(p.id()));
                existing.getAllowedProduct().add(rap);
            });
        }

        ResolutionEntity saved = resolutionRepository.save(existing);
        return ResponseEntity.ok(APIResponseDTO.success(ResolutionMapper.toDto(saved), constants.success.savedSuccess));
    }

    @Transactional
    public ResponseEntity<APIResponseDTO<ResolutionDTO>> changeStatus(Long id) {
        ResolutionEntity existing = resolutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(constants.messages.noData));
        existing.setIsActive(!existing.getIsActive());
        ResolutionEntity saved = resolutionRepository.save(existing);
        return ResponseEntity.ok(APIResponseDTO.success(ResolutionMapper.toDto(saved), constants.success.savedSuccess));
    }
}
