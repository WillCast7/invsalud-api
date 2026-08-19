package com.aurealab.service;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.VectorialDocumentDTO;
import com.aurealab.dto.VectorialDocumentRequestDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface VectorialDocumentService {
    ResponseEntity<APIResponseDTO<Map<String, Object>>> getDocuments(int page, int size, String search, String moduleCode);
    ResponseEntity<APIResponseDTO<VectorialDocumentDTO>> createDocument(VectorialDocumentRequestDTO request);
    ResponseEntity<APIResponseDTO<VectorialDocumentDTO>> replaceDocument(Long id, VectorialDocumentRequestDTO request);
    ResponseEntity<APIResponseDTO<String>> deleteDocument(Long id);
    ResponseEntity<APIResponseDTO<List<VectorialDocumentDTO>>> getChunksByDocumentTitle(String title);
}
