package com.aurealab.service.impl;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ProductCategoryDTO;
import com.aurealab.mapper.CashRegister.ProductCategoryMapper;
import com.aurealab.model.cashRegister.entity.ProductCategoryEntity;
import com.aurealab.model.cashRegister.repository.ProductCategoryRepository;
import com.aurealab.service.ProductCategoryService;
import com.aurealab.service.impl.shared.TenantService;
import com.aurealab.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private JwtUtils  jwtUtils;

    @Override
    public ResponseEntity<APIResponseDTO<List<ProductCategoryDTO>>> findAllCategories() {
        try {
            return tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
                List<ProductCategoryEntity> entities = productCategoryRepository.findAll();
                List<ProductCategoryDTO> dtos = entities.stream()
                        .map(ProductCategoryMapper::toDto)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(APIResponseDTO.success(dtos,"Categorías obtenidas correctamente"));
            });
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponseDTO.failure("Error al obtener las categorías: ", e.getMessage()));
        }
    }
}
