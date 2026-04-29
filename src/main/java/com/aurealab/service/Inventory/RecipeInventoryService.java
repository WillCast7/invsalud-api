package com.aurealab.service.Inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.RecipeInventoryDTO;
import com.aurealab.model.inventory.entity.RecipeInventoryEntity;
import org.springframework.http.ResponseEntity;

public interface RecipeInventoryService {
    public RecipeInventoryEntity save(RecipeInventoryEntity recipeInventoryDTO);
    public ResponseEntity<APIResponseDTO<RecipeInventoryDTO>> findById();
    public RecipeInventoryEntity findByIdEntity();

}
