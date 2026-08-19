package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.VectorialDocumentDTO;
import com.aurealab.dto.VectorialDocumentRequestDTO;
import com.aurealab.service.VectorialDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rag/documents")
public class VectorialDocumentController {

    @Autowired
    private VectorialDocumentService documentService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<Map<String, Object>>> getDocuments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String moduleCode) {
        return documentService.getDocuments(page, size, search, moduleCode);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponseDTO<VectorialDocumentDTO>> createDocument(
            @RequestBody VectorialDocumentRequestDTO request) {
        return documentService.createDocument(request);
    }

    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponseDTO<VectorialDocumentDTO>> updateDocument(
            @PathVariable Long id,
            @RequestBody VectorialDocumentRequestDTO request) {
        return documentService.replaceDocument(id, request);
    }

    @PostMapping(value = "/{id}/replace", consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponseDTO<VectorialDocumentDTO>> replaceDocument(
            @PathVariable Long id,
            @RequestBody VectorialDocumentRequestDTO request) {
        return documentService.replaceDocument(id, request);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<APIResponseDTO<String>> deleteDocument(@PathVariable Long id) {
        return documentService.deleteDocument(id);
    }

    @GetMapping(value = "/chunks", produces = "application/json")
    public ResponseEntity<APIResponseDTO<List<VectorialDocumentDTO>>> getChunksByTitle(
            @RequestParam String title) {
        return documentService.getChunksByDocumentTitle(title);
    }
}
