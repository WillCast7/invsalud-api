package com.aurealab.service.Inventory;

import com.aurealab.dto.RecipeInventoryDTO;
import com.aurealab.model.inventory.entity.RecipeInventoryEntity;

public interface RecipeInventoryService {
    public RecipeInventoryEntity save(RecipeInventoryEntity recipeInventoryDTO);
    public RecipeInventoryEntity findById();

}
