package com.aurealab.service;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ProductCategoryDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductCategoryService {
    ResponseEntity<APIResponseDTO<List<ProductCategoryDTO>>> findAllCategories();
}
