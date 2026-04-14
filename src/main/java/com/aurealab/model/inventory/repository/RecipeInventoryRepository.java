package com.aurealab.model.inventory.repository;

import com.aurealab.model.inventory.entity.RecipeInventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeInventoryRepository extends JpaRepository<RecipeInventoryEntity, Long> {

}
