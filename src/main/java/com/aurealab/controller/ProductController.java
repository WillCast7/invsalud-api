package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping(produces = "application/json", value = "/changestatus/{id}")
    public ResponseEntity<APIResponseDTO<ProductDTO>> changeStatus(@PathVariable Long id) {
        return productService.changeStatus(id);
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<ProductDTO>> SaveProduct(@RequestBody ProductDTO product) {
        return productService.saveProduct(product);
    }
}
