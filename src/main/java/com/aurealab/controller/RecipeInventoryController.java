package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.RecipeInventoryDTO;
import com.aurealab.service.Inventory.RecipeInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipes")
public class RecipeInventoryController {

    @Autowired
    RecipeInventoryService recipeInventoryService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<RecipeInventoryDTO>> getPurchasingForTable() {
        return recipeInventoryService.findById();
    }
}
