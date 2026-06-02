package com.aurealab.service.impl.inventory;

import com.aurealab.dto.PrescriptionInventoryTableDTO;
import com.aurealab.model.inventory.entity.PurchasingRecipeEntity;
import com.aurealab.model.inventory.repository.PurchasingRecipeRepository;
import com.aurealab.model.specs.PurchasingRecipeSpecs;
import com.aurealab.service.Inventory.PurchasingRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchasingRecipeServiceImpl implements PurchasingRecipeService {

    @Autowired
    PurchasingRecipeRepository purchasingRecipeRepository;

    public PurchasingRecipeEntity savePurchasingEntity(PurchasingRecipeEntity purchasingRecipeEntity){
        return purchasingRecipeRepository.save(purchasingRecipeEntity);
    }

    @Override
    @Transactional
    public Page<PrescriptionInventoryTableDTO> getPurchasingRecipeReport(
            int page, int size, LocalDateTime start, LocalDateTime end,
            String documentNumber, String product) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Specification<PurchasingRecipeEntity> spec = PurchasingRecipeSpecs.reportSearch(start, end, documentNumber, product);
        Page<PurchasingRecipeEntity> recipePage = purchasingRecipeRepository.findAll(spec, pageable);

        List<PrescriptionInventoryTableDTO> dtoList = new ArrayList<>();
        for (PurchasingRecipeEntity recipe : recipePage.getContent()) {
            dtoList.add(mapRecipeToTableDto(recipe));
        }

        return new org.springframework.data.domain.PageImpl<>(dtoList, pageable, recipePage.getTotalElements());
    }

    private PrescriptionInventoryTableDTO mapRecipeToTableDto(PurchasingRecipeEntity recipe) {
        String serialRange = "N/A";
        if (recipe.getStartSerial() != null && recipe.getFinalSerial() != null) {
            serialRange = "Seriales: " + recipe.getStartSerial() + " - " + recipe.getFinalSerial();
        }

        return PrescriptionInventoryTableDTO.builder()
                .id(recipe.getId())
                .product("Recetarios")
                .presentation("N/A")
                .pharmaceuticalForm("N/A")
                .batch(serialRange)
                .purchasePrice(recipe.getPriceUnit())
                .salePrice(null)
                .totalUnits((long) recipe.getUnits())
                .availableUnits((long) recipe.getUnits())
                .expirationDate(null)
                .isActive(recipe.getPurchasing() != null && recipe.getPurchasing().getIsActive() != null ? recipe.getPurchasing().getIsActive() : true)
                .build();
    }
}
