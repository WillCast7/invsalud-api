package com.aurealab.service.impl.inventory;

import com.aurealab.dto.RecipeInventoryDTO;
import com.aurealab.mapper.inventory.RecipeInventoryMapper;
import com.aurealab.model.inventory.entity.RecipeInventoryEntity;
import com.aurealab.model.inventory.repository.RecipeInventoryRepository;
import com.aurealab.service.Inventory.RecipeInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
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

    public RecipeInventoryEntity findById(){
        return recipeInventoryRepository.findById(1L).get();
    }
}
