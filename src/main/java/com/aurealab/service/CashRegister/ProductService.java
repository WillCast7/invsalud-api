package com.aurealab.service.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.model.cashRegister.entity.ProductEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    public List<ProductEntity> findAllProducts();
    public List<ProductDTO> findProductsByType(String type);
    public ResponseEntity<APIResponseDTO<String>> findPaginatedProducts(int page, int size, String searchValue);
    public ResponseEntity<APIResponseDTO<ProductDTO>> changeStatus(Long id);
    public ResponseEntity<APIResponseDTO<ProductDTO>> saveProduct(ProductDTO product);
}
