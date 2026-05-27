package com.aurealab.mapper;

import com.aurealab.dto.DocumentTemplateDTO;
import com.aurealab.dto.tables.DocumentTemplateTableDTO;
import com.aurealab.model.aurea.entity.DocumentTemplateEntity;

public class DocumentTemplateMapper {
    private DocumentTemplateMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static DocumentTemplateDTO toDto(DocumentTemplateEntity entity) {
        if (entity == null) return null;

        return new DocumentTemplateDTO(
                entity.getId(),
                entity.getName(),
                entity.getDocumentType(),
                entity.getCategory(),
                entity.getHtmlContent(),
                entity.getCssContent(),
                entity.getSectionsState(),
                entity.getIsDefault(),
                entity.getIsActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCreatedBy(),
                entity.getVersion()
        );
    }

    /* ===================== Entity -> TableDTO ===================== */
    public static DocumentTemplateTableDTO toTableDto(DocumentTemplateEntity entity) {
        if (entity == null) return null;

        return new DocumentTemplateTableDTO(
                entity.getId(),
                entity.getName(),
                entity.getDocumentType(),
                entity.getCategory(),
                entity.getIsDefault(),
                entity.getIsActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static DocumentTemplateEntity toEntity(DocumentTemplateDTO dto) {
        if (dto == null) return null;

        DocumentTemplateEntity entity = new DocumentTemplateEntity();
        entity.setId(dto.id());
        entity.setName(dto.name());
        entity.setDocumentType(dto.documentType());
        entity.setCategory(dto.category());
        entity.setHtmlContent(dto.htmlContent());
        entity.setCssContent(dto.cssContent());
        entity.setSectionsState(dto.sectionsState());
        entity.setIsDefault(dto.isDefault());
        entity.setIsActive(dto.isActive());
        entity.setCreatedAt(dto.createdAt());
        entity.setUpdatedAt(dto.updatedAt());
        entity.setCreatedBy(dto.createdBy());
        entity.setVersion(dto.version());

        return entity;
    }
}
