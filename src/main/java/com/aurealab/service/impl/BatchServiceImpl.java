package com.aurealab.service.impl;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.BatchDTO;
import com.aurealab.dto.BatchDTO;
import com.aurealab.mapper.inventory.BatchMapper;
import com.aurealab.model.inventory.entity.BatchEntity;
import com.aurealab.model.inventory.entity.BatchEntity;
import com.aurealab.model.inventory.repository.BatchRepository;
import com.aurealab.model.specs.BatchSpecs;
import com.aurealab.service.Inventory.BatchService;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BatchServiceImpl implements BatchService {

    @Autowired
    BatchRepository batchRepository;
    @Autowired
    private JwtUtils jwtUtils;


    public ResponseEntity<APIResponseDTO<String>> findPaginatedBatches(int page, int size, String searchValue){

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return ResponseEntity.ok(APIResponseDTO.withPageable(constants.success.findedSuccess, constants.success.findedSuccess,
                batchRepository.findAll(BatchSpecs.search(searchValue), pageable)));
    };


    public ResponseEntity<APIResponseDTO<BatchDTO>> changeStatus(Long id){
        BatchEntity batchSearched = getBatchEntityById(id);
        batchSearched.setIsActive(!batchSearched.getIsActive());
        return ResponseEntity.ok(
                APIResponseDTO.success(
                        BatchMapper.toDto(
                                batchRepository.save(batchSearched)
                        ), constants.success.findedSuccess
                )
        );
    }

    public ResponseEntity<APIResponseDTO<BatchDTO>> saveBatch(BatchDTO batch){
        BatchEntity batchEntity = BatchMapper.toEntity(batch);
        batchEntity.setCreatedBy(jwtUtils.getCurrentUserId());
        return ResponseEntity.ok(
                APIResponseDTO.success(
                        BatchMapper.toDto(
                                batchRepository.save(batchEntity)
                        ),
                        constants.success.savedSuccess
                )
        );
    }

    public BatchEntity getBatchEntityById(Long id){
        Optional<BatchEntity> productOptional =  batchRepository.findById(id);

        if (productOptional.isPresent()){
            return productOptional.get();
        } else {
            throw new RuntimeException(constants.messages.noData);
        }
    }

}
