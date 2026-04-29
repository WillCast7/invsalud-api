package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.dto.PrescriptionInventoryDTO;
import com.aurealab.dto.response.ProductWithParamsResponseDTO;
import com.aurealab.service.Inventory.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<String>> getProducts(@RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int size,
                                                              @RequestParam(defaultValue = "") String searchValue) {
        return productService.findPaginatedProducts(page, size, searchValue);
    }

    @GetMapping(produces = "application/json", value = "/{id}")
    public ResponseEntity<APIResponseDTO<ProductWithParamsResponseDTO>> getProduct(@PathVariable Long id) {
        return productService.findProductById(id);
    }
    @GetMapping(produces = "application/json", value = "/byresolution/{id}")
    public ResponseEntity<APIResponseDTO<Set<PrescriptionInventoryDTO>>> getProductsByResolution(@PathVariable Long id) {
        return productService.getResolutionProductById(id);
    }

    @PutMapping(produces = "application/json", value = "/changestatus/{id}")
    public ResponseEntity<APIResponseDTO<ProductDTO>> changeStatus(@PathVariable Long id) {
        return productService.changeStatus(id);
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<ProductDTO>> SaveProduct(@RequestBody ProductDTO product) {
        return productService.saveProduct(product);
    }
}
