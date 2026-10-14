package com.aurealab.dto;

import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public record ResolutionDTO(
    Long id,
    Set<ThirdPartyDTO> thirdParties,
    String code,
    LocalDate startDate,
    LocalDate expirationDate,
    String description,
    Boolean isActive,
    LocalDateTime createdAt,
    String createdBy,
    Set<ProductDTO> products,
    String numberResolution
) {
    @JsonCreator
    public ResolutionDTO(
            @JsonProperty("id") Long id,
            @JsonProperty("thirdParties") Set<ThirdPartyDTO> thirdParties,
            @JsonProperty("thirdParty") ThirdPartyDTO thirdParty,
            @JsonProperty("code") String code,
            @JsonProperty("startDate") LocalDate startDate,
            @JsonProperty("expirationDate") LocalDate expirationDate,
            @JsonProperty("description") String description,
            @JsonProperty("isActive") Boolean isActive,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("createdBy") String createdBy,
            @JsonProperty("products") Set<ProductDTO> products,
            @JsonProperty("numberResolution") String numberResolution
    ) {
        this(
                id,
                mergeThirdParties(thirdParties, thirdParty),
                code,
                startDate,
                expirationDate,
                description,
                isActive,
                createdAt,
                createdBy,
                products,
                numberResolution
        );
    }

    private static Set<ThirdPartyDTO> mergeThirdParties(Set<ThirdPartyDTO> thirdParties, ThirdPartyDTO thirdParty) {
        Set<ThirdPartyDTO> set = new HashSet<>();
        if (thirdParties != null) {
            set.addAll(thirdParties);
        }
        if (thirdParty != null && thirdParty.id() != null) {
            set.add(thirdParty);
        }
        return set;
    }

    public ThirdPartyDTO thirdParty() {
        return (thirdParties != null && !thirdParties.isEmpty()) ? thirdParties.iterator().next() : null;
    }
}
