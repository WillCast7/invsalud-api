package com.aurealab.service.impl.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.mapper.CashRegister.ProductMapper;
import com.aurealab.model.cashRegister.entity.ProductEntity;
import com.aurealab.model.cashRegister.repository.ProductRepository;
import com.aurealab.model.cashRegister.specs.ProductSpecs;
import com.aurealab.service.CashRegister.ProductService;
import com.aurealab.service.impl.shared.TenantService;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    TenantService tenantService;

    @Autowired
    JwtUtils jwtUtils;

    public List<ProductEntity> findAllProducts(){
        return tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            return productRepository.findAll();
        });
    };

    public ResponseEntity<APIResponseDTO<String>> findPaginatedProducts(int page, int size, String searchValue){

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return ResponseEntity.ok(APIResponseDTO.withPageable(constants.success.findedSuccess, constants.success.findedSuccess, tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            return productRepository.findAll(ProductSpecs.search(searchValue), pageable);
        })));
    };

    public List<ProductDTO> findProductsByType(String type){
        List<ProductDTO> productDTOS = new ArrayList<>();
        tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            return productRepository.findAllByType(type);
        }).forEach(productEntity -> productDTOS.add(ProductMapper.toDto(productEntity)));

        return productDTOS;
    };

    public ResponseEntity<APIResponseDTO<ProductDTO>> changeStatus(Long id){
        ProductEntity productSearched = getProductEntityById(id);
        productSearched.setActive(!productSearched.isActive());
          return ResponseEntity.ok(
                  APIResponseDTO.success(
                      ProductMapper.toDto(
                          tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
                            return productRepository.save(productSearched);
                          })
                      ), constants.success.findedSuccess
                  )
          );
    }

    public ResponseEntity<APIResponseDTO<ProductDTO>> saveProduct(ProductDTO product){
        return ResponseEntity.ok(
            APIResponseDTO.success(
                ProductMapper.toDto(
                    tenantService.executeInTenant(
                        jwtUtils.getCurrentTenant(), () -> {
                            return productRepository.save(
                                ProductMapper.toEntity(
                                    product
                                )
                            );
                        }
                    )
                ),
                constants.success.savedSuccess
            )
        );
    }

    public ProductEntity getProductEntityById(Long id){
        Optional<ProductEntity> productOptional = tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            return productRepository.findById(id);
        });

        if (productOptional.isPresent()){
            return productOptional.get();
        } else {
            throw new RuntimeException(constants.messages.noData);
        }
    }
}
