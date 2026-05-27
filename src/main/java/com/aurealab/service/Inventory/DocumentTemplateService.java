package com.aurealab.service.Inventory;

import com.aurealab.dto.DocumentTemplateDTO;
import com.aurealab.dto.tables.DocumentTemplateTableDTO;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface DocumentTemplateService {
    Page<DocumentTemplateTableDTO> getTemplates(int page, int size, String searchValue);
    DocumentTemplateDTO getTemplateById(UUID id);
    DocumentTemplateDTO createTemplate(DocumentTemplateDTO dto);
    DocumentTemplateDTO updateTemplate(UUID id, DocumentTemplateDTO dto);
    void setTemplateDefaultId(UUID id);
}
