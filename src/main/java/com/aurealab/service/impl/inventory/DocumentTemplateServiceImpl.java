package com.aurealab.service.impl.inventory;

import com.aurealab.dto.DocumentTemplateDTO;
import com.aurealab.dto.tables.DocumentTemplateTableDTO;
import com.aurealab.mapper.DocumentTemplateMapper;
import com.aurealab.model.aurea.entity.DocumentTemplateEntity;
import com.aurealab.model.aurea.repository.DocumentTemplateRepository;
import com.aurealab.service.Inventory.DocumentTemplateService;
import com.aurealab.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentTemplateServiceImpl implements DocumentTemplateService {

    @Autowired
    private DocumentTemplateRepository documentTemplateRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Page<DocumentTemplateTableDTO> getTemplates(int page, int size, String searchValue) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DocumentTemplateEntity> entityPage;
        
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            entityPage = documentTemplateRepository.findByNameContainingIgnoreCase(searchValue, pageable);
        } else {
            entityPage = documentTemplateRepository.findAll(pageable);
        }
        
        return entityPage.map(DocumentTemplateMapper::toTableDto);
    }

    @Override
    public DocumentTemplateDTO getTemplateById(UUID id) {
        DocumentTemplateEntity entity = documentTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plantilla no encontrada"));
        return DocumentTemplateMapper.toDto(entity);
    }

    @Override
    @Transactional
    public DocumentTemplateDTO createTemplate(DocumentTemplateDTO dto) {
        DocumentTemplateEntity entity = DocumentTemplateMapper.toEntity(dto);
        entity.setId(null); // Asegurar que es nuevo
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setIsActive(true);
        entity.setCreatedBy(jwtUtils.getCurrentUserId());

        // Si es el primero, podría ser default (opcional)
        // Por ahora mantenemos la lógica base
        entity = documentTemplateRepository.save(entity);
        return DocumentTemplateMapper.toDto(entity);
    }

    @Override
    @Transactional
    public DocumentTemplateDTO updateTemplate(UUID id, DocumentTemplateDTO dto) {
        DocumentTemplateEntity entity = documentTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plantilla no encontrada"));
                
        entity.setName(dto.name());
        entity.setDocumentType(dto.documentType());
        entity.setCategory(dto.category());
        entity.setHtmlContent(dto.htmlContent());
        entity.setCssContent(dto.cssContent());
        entity.setSectionsState(dto.sectionsState());
        entity.setIsActive(dto.isActive() != null ? dto.isActive() : entity.getIsActive());
        entity.setUpdatedAt(LocalDateTime.now());
        
        entity = documentTemplateRepository.save(entity);
        return DocumentTemplateMapper.toDto(entity);
    }

    @Override
    @Transactional
    public void setTemplateDefaultId(UUID id) {
        DocumentTemplateEntity newDefault = documentTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plantilla no encontrada"));
                
        // Buscar todas las de la misma categoría y tipo
        List<DocumentTemplateEntity> similarTemplates = documentTemplateRepository
                .findByDocumentTypeAndCategory(newDefault.getDocumentType(), newDefault.getCategory());
                
        // Quitar default a todas
        for (DocumentTemplateEntity template : similarTemplates) {
            template.setIsDefault(false);
        }
        documentTemplateRepository.saveAll(similarTemplates);
        
        // Asignar default a la seleccionada
        newDefault.setIsDefault(true);
        documentTemplateRepository.save(newDefault);
    }
}
