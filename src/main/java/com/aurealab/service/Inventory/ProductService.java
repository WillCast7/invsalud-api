package com.aurealab.service.Inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.dto.response.ProductWithParamsResponseDTO;
import com.aurealab.model.inventory.entity.ProductEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    public ResponseEntity<APIResponseDTO<ProductWithParamsResponseDTO>> findProductById(Long id);
    public ResponseEntity<APIResponseDTO<String>> findPaginatedProducts(int page, int size, String searchValue);
    public ResponseEntity<APIResponseDTO<ProductDTO>> changeStatus(Long id);
    public ResponseEntity<APIResponseDTO<ProductDTO>> saveProduct(ProductDTO product);
}
