package com.aurealab.model.inventory.repository;

import com.aurealab.model.inventory.entity.PurchasingRecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PurchasingRecipeRepository extends JpaRepository<PurchasingRecipeEntity, Long>, JpaSpecificationExecutor<PurchasingRecipeEntity> {
}
