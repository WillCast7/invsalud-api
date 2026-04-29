package com.aurealab.mapper;

import com.aurealab.dto.CompanyConfigDTO;
import com.aurealab.model.aurea.entity.CompanyConfigEntity;

public class CompanyConfigMapper {

    public static CompanyConfigDTO toDto(CompanyConfigEntity entity) {
        if (entity == null) {
            return null;
        }
        return new CompanyConfigDTO(
                entity.getId(),
                entity.isFollowCase(),
                entity.isFollowProduct()
        );
    }

    public static CompanyConfigEntity toEntity(CompanyConfigDTO dto) {
        if (dto == null) {
            return null;
        }
        CompanyConfigEntity entity = new CompanyConfigEntity();
        entity.setId(dto.id());
        entity.setFollowCase(dto.followCase());
        entity.setFollowProduct(dto.followProduct());
        return entity;
    }
}
