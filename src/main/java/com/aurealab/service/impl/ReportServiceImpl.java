package com.aurealab.service.impl;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.PrescriptionInventoryTableDTO;
import com.aurealab.model.inventory.entity.RecipeInventoryEntity;
import com.aurealab.service.ReportService;
import com.aurealab.service.Inventory.OrderService;
import com.aurealab.service.Inventory.PurchasingService;
import com.aurealab.service.Inventory.PurchasingRecipeService;
import com.aurealab.service.Inventory.PrescriptionInventoryService;
import com.aurealab.service.Inventory.RecipeInventoryService;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PurchasingService purchasingService;

    @Autowired
    private PurchasingRecipeService purchasingRecipeService;

    @Autowired
    private PrescriptionInventoryService prescriptionInventoryService;

    @Autowired
    private RecipeInventoryService recipeInventoryService;

    @Override
    public ResponseEntity<APIResponseDTO<String>> getReport(
            int page, int size, String type, String category,
            String startDate, String endDate, String documentNumber,
            String product, String batch) {

        // 2. Parse Dates safely
        LocalDateTime start = null;
        if (startDate != null && !startDate.trim().isEmpty()) {
            try {
                start = LocalDate.parse(startDate).atStartOfDay();
            } catch (Exception e) {
                // Ignore or log date parsing error
            }
        }
        LocalDateTime end = null;
        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
            } catch (Exception e) {
                // Ignore or log date parsing error
            }
        }

        Page<PrescriptionInventoryTableDTO> pageResult;

        // 3. Delegate to appropriate service based on logic
        if ("order".equalsIgnoreCase(type)) {
            // Quotations
            pageResult = orderService.getOrdersReport(
                    page, size, false, category, start, end, documentNumber, product, batch);
        } else if ("sold".equalsIgnoreCase(type)) {
            // Sales
            pageResult = orderService.getOrdersReport(
                    page, size, true, category, start, end, documentNumber, product, batch);
        } else if ("purchasing".equalsIgnoreCase(type)) {
            // Purchases
            if ("recipe".equalsIgnoreCase(category)) {
                pageResult = purchasingRecipeService.getPurchasingRecipeReport(
                        page, size, start, end, documentNumber, product);
            } else {
                pageResult = purchasingService.getPurchasingReport(
                        page, size, category, start, end, documentNumber, product, batch);
            }
        } else {
            // "Todos" type -> Fallback query using PrescriptionInventoryService
            if ("recipe".equalsIgnoreCase(category)) {
                RecipeInventoryEntity recipe = recipeInventoryService.findByIdEntity();
                List<PrescriptionInventoryTableDTO> list = new ArrayList<>();
                if (recipe != null) {
                    list.add(PrescriptionInventoryTableDTO.builder()
                            .id(recipe.getId())
                            .product("Recetarios")
                            .presentation("N/A")
                            .pharmaceuticalForm("N/A")
                            .batch("N/A")
                            .purchasePrice(BigDecimal.ZERO)
                            .salePrice(recipe.getPrice())
                            .totalUnits((long) recipe.getTotalUnits())
                            .availableUnits((long) recipe.getAvaliableUnits())
                            .expirationDate(null)
                            .isActive(true)
                            .build());
                }
                Pageable pageable = PageRequest.of(page, size);
                pageResult = new org.springframework.data.domain.PageImpl<>(list, pageable, list.size());
            } else {
                Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
                String fallbackSearch = (product != null && !product.isEmpty()) ? product : ((batch != null && !batch.isEmpty()) ? batch : "");
                pageResult = prescriptionInventoryService.findAllToTable(pageable, fallbackSearch, category);
            }
        }

        return ResponseEntity.ok(APIResponseDTO.withPageable(
                constants.success.findedSuccess,
                constants.success.findedSuccess,
                pageResult
        ));
    }
}
