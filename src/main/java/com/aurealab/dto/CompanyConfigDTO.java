package com.aurealab.dto;

public record CompanyConfigDTO(
    Long id,
    boolean followCase,
    boolean followProduct
) {
}
