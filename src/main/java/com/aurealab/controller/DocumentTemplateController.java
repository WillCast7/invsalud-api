package com.aurealab.controller;

import com.aurealab.dto.DocumentTemplateDTO;
import com.aurealab.dto.tables.DocumentTemplateTableDTO;
import com.aurealab.service.Inventory.DocumentTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/document-templates")
public class DocumentTemplateController {

    @Autowired
    private DocumentTemplateService documentTemplateService;

    @GetMapping(value = "/check-default")
    public ResponseEntity<Map<String, Object>> checkDefault(
            @RequestParam String documentType,
            @RequestParam String category,
            @RequestParam(required = false) String excludeId){
        UUID excludeUuid = null;
        if (excludeId != null && !excludeId.trim().isEmpty() && !"null".equalsIgnoreCase(excludeId.trim()) && !"undefined".equalsIgnoreCase(excludeId.trim())) {
            try {
                excludeUuid = UUID.fromString(excludeId.trim());
            } catch (IllegalArgumentException ignored) {}
        }
        return ResponseEntity.ok(documentTemplateService.checkDefault(documentType, category, excludeUuid));
    }

    @PutMapping(value = "/{id}/set-default")
    public ResponseEntity<Void> setTemplateDefaultId(@PathVariable UUID id){
        documentTemplateService.setTemplateDefaultId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DocumentTemplateDTO> getTemplateById(@PathVariable UUID id){
        return ResponseEntity.ok(documentTemplateService.getTemplateById(id));
    }

    @GetMapping
    public ResponseEntity<Page<DocumentTemplateTableDTO>> getTemplates(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String searchValue){
        return ResponseEntity.ok(documentTemplateService.getTemplates(page, size, searchValue));
    }

    @PostMapping
    public ResponseEntity<DocumentTemplateDTO> createTemplate(@RequestBody DocumentTemplateDTO dto){
        return ResponseEntity.ok(documentTemplateService.createTemplate(dto));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<DocumentTemplateDTO> updateTemplate(@PathVariable UUID id, @RequestBody DocumentTemplateDTO dto){
        return ResponseEntity.ok(documentTemplateService.updateTemplate(id, dto));
    }
}
