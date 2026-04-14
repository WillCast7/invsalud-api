package com.aurealab.service.impl.inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.dto.response.ProductWithParamsResponseDTO;
import com.aurealab.mapper.inventory.ProductMapper;
import com.aurealab.model.inventory.entity.ProductEntity;
import com.aurealab.model.inventory.repository.ProductRepository;
import com.aurealab.model.specs.ProductSpecs;
import com.aurealab.service.ConfigParamService;
import com.aurealab.service.Inventory.ProductService;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ConfigParamService configParamService;
    @Autowired
    private JwtUtils jwtUtils;

    public List<ProductEntity> findAllProducts(){
            return productRepository.findAll();
    };

    public ResponseEntity<APIResponseDTO<ProductWithParamsResponseDTO>> findProductById(Long id){
        ProductWithParamsResponseDTO response = ProductWithParamsResponseDTO.builder()
                .product(ProductMapper.toDto(getProductEntityById(id)))
                .configParams(configParamService.findParamsByParent(constants.configParam.pharmaceuticForm))
                .build();

        return ResponseEntity.ok(APIResponseDTO.success(
                response,
                constants.success.findedSuccess
        ));
    }

    public ResponseEntity<APIResponseDTO<String>> findPaginatedProducts(int page, int size, String searchValue){

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return ResponseEntity.ok(APIResponseDTO.withPageable(constants.success.findedSuccess, constants.success.findedSuccess,
             productRepository.findAll(ProductSpecs.search(searchValue), pageable).map(ProductMapper::toDto)));
    };

    public ResponseEntity<APIResponseDTO<ProductDTO>> changeStatus(Long id){
        ProductEntity productSearched = getProductEntityById(id);
        productSearched.setIsActive(!productSearched.getIsActive());
          return ResponseEntity.ok(
                  APIResponseDTO.success(
                      ProductMapper.toDto(
                          productRepository.save(productSearched)
                      ), constants.success.findedSuccess
                  )
          );
    }

    public ResponseEntity<APIResponseDTO<ProductDTO>> saveProduct(ProductDTO product){
        ProductEntity productEntity = ProductMapper.toEntity(product);
        productEntity.setCreatedBy(jwtUtils.getCurrentUserId());
        return ResponseEntity.ok(
            APIResponseDTO.success(
                ProductMapper.toDto(
                    productRepository.save(productEntity)
                ),
                constants.success.savedSuccess
            )
        );
    }

    public ProductEntity getProductEntityById(Long id){
        Optional<ProductEntity> productOptional =  productRepository.findById(id);

        if (productOptional.isPresent()){
            return productOptional.get();
        } else {
            throw new RuntimeException(constants.messages.noData);
        }
    }
}
