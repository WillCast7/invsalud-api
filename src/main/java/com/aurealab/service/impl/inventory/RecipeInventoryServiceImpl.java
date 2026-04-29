package com.aurealab.service.impl.inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.RecipeInventoryDTO;
import com.aurealab.mapper.inventory.RecipeInventoryMapper;
import com.aurealab.model.inventory.entity.RecipeInventoryEntity;
import com.aurealab.model.inventory.repository.RecipeInventoryRepository;
import com.aurealab.service.Inventory.RecipeInventoryService;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RecipeInventoryServiceImpl implements RecipeInventoryService {

    @Autowired
    RecipeInventoryRepository recipeInventoryRepository;

    public RecipeInventoryEntity save(RecipeInventoryEntity recipeInventoryEntity) {
        return
                recipeInventoryRepository.save(
                        recipeInventoryEntity
                );
    }

    public ResponseEntity<APIResponseDTO<RecipeInventoryDTO>> findById(){
        return ResponseEntity.ok(APIResponseDTO.success(RecipeInventoryMapper.toDto(findByIdEntity()), constants.messages.consultGood)) ;
    }

    public RecipeInventoryEntity findByIdEntity(){
        return recipeInventoryRepository.findById(1L).get();
    }
}
