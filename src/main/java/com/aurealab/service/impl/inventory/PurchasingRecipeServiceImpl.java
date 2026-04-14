package com.aurealab.service.impl.inventory;

import com.aurealab.model.inventory.entity.PurchasingRecipeEntity;
import com.aurealab.model.inventory.repository.PurchasingRecipeRepository;
import com.aurealab.service.Inventory.PurchasingRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchasingRecipeServiceImpl implements PurchasingRecipeService {

    @Autowired
    PurchasingRecipeRepository purchasingRecipeRepository;

    public PurchasingRecipeEntity savePurchasingEntity(PurchasingRecipeEntity purchasingRecipeEntity){
        return purchasingRecipeRepository.save(purchasingRecipeEntity);
    }
}
