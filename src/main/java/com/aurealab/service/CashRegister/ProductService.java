package com.aurealab.service.CashRegister;

import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.model.cashRegister.entity.ProductEntity;

import java.util.List;

public interface ProductService {
    public List<ProductEntity> findAllProducts();
    public List<ProductDTO> findProductsByType(String type);
}
