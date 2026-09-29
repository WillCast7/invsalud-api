package com.aurealab.model.aurea.repository;

import com.aurealab.model.aurea.entity.DocumentTemplateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplateEntity, UUID> {
    
    Page<DocumentTemplateEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    List<DocumentTemplateEntity> findByDocumentType(String documentType);

    List<DocumentTemplateEntity> findByDocumentTypeAndCategory(String documentType, String category);

    List<DocumentTemplateEntity> findByDocumentTypeIn(List<String> documentTypes);

    java.util.Optional<DocumentTemplateEntity> findByCategoryAndIsDefault(String category, Boolean isDefault);

    java.util.Optional<DocumentTemplateEntity> findByDocumentTypeAndCategoryAndIsDefault(String documentType, String category, Boolean isDefault);
}
