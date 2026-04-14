package com.aurealab.dto;

import com.aurealab.model.inventory.entity.ProductEntity;
import com.aurealab.model.inventory.entity.ResolutionEntity;

public record ResolutionAllowedProductDTO(
    Long id,
    ResolutionEntity resolution,
    ProductEntity product
) {
}
