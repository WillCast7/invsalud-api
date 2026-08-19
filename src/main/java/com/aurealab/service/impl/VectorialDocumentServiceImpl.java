package com.aurealab.service.impl;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.VectorialDocumentDTO;
import com.aurealab.dto.VectorialDocumentRequestDTO;
import com.aurealab.model.aurea.entity.VectorialDocumentEntity;
import com.aurealab.model.aurea.repository.VectorialDocumentRepository;
import com.aurealab.service.VectorialDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VectorialDocumentServiceImpl implements VectorialDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(VectorialDocumentServiceImpl.class);

    @Autowired
    private VectorialDocumentRepository repository;

    @Override
    public ResponseEntity<APIResponseDTO<Map<String, Object>>> getDocuments(int page, int size, String search, String moduleCode) {
        try {
            Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by("id").descending());
            
            String searchClean = (search != null && !search.trim().isEmpty()) ? search.trim().toLowerCase() : null;
            String moduleClean = (moduleCode != null && !moduleCode.trim().isEmpty()) ? moduleCode.trim() : null;

            Page<VectorialDocumentEntity> entityPage;
            if (searchClean != null) {
                entityPage = repository.searchDocuments(searchClean, moduleClean, pageable);
            } else if (moduleClean != null) {
                entityPage = repository.findByModuleCodeAndActiveTrue(moduleClean, pageable);
            } else {
                entityPage = repository.findByActiveTrue(pageable);
            }

            // Group by documentTitle to present aggregated document view
            Map<String, List<VectorialDocumentEntity>> grouped = entityPage.getContent().stream()
                    .collect(Collectors.groupingBy(
                            VectorialDocumentEntity::getDocumentTitle,
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            List<VectorialDocumentDTO> documentDTOs = new ArrayList<>();
            for (Map.Entry<String, List<VectorialDocumentEntity>> entry : grouped.entrySet()) {
                List<VectorialDocumentEntity> list = entry.getValue();
                VectorialDocumentEntity main = list.get(0);

                VectorialDocumentDTO dto = VectorialDocumentDTO.builder()
                        .id(main.getId())
                        .documentTitle(main.getDocumentTitle())
                        .chunkContent(main.getChunkContent())
                        .moduleCode(main.getModuleCode())
                        .vectorEmbedding(main.getVectorEmbedding())
                        .version(main.getVersion() != null ? main.getVersion() : "1.0")
                        .status(main.getStatus() != null ? main.getStatus() : "PROCESADO")
                        .metadata(main.getMetadata())
                        .fileType(main.getFileType() != null ? main.getFileType() : "txt")
                        .fileSize(main.getFileSize())
                        .active(main.getActive())
                        .createdAt(main.getCreatedAt())
                        .updatedAt(main.getUpdatedAt())
                        .chunkCount(list.size())
                        .build();

                documentDTOs.add(dto);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("documents", documentDTOs);
            response.put("currentPage", entityPage.getNumber() + 1);
            response.put("totalItems", entityPage.getTotalElements());
            response.put("totalPages", entityPage.getTotalPages());

            return ResponseEntity.ok(APIResponseDTO.success(response, "Documentos RAG obtenidos exitosamente"));

        } catch (Exception e) {
            logger.error("Error al consultar documentos vectoriales RAG", e);
            return ResponseEntity.internalServerError().body(
                    APIResponseDTO.failure("Error al consultar los documentos vectoriales: " + e.getMessage(), "RAG_READ_ERROR")
            );
        }
    }

    @Override
    @Transactional
    public ResponseEntity<APIResponseDTO<VectorialDocumentDTO>> createDocument(VectorialDocumentRequestDTO request) {
        try {
            if (request.getDocumentTitle() == null || request.getDocumentTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        APIResponseDTO.failure("El título del documento es obligatorio", "INVALID_TITLE")
                );
            }

            List<String> chunks = prepareChunks(request);
            if (chunks.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        APIResponseDTO.failure("El contenido del documento no puede estar vacío", "EMPTY_CONTENT")
                );
            }

            String version = request.getVersion() != null && !request.getVersion().trim().isEmpty()
                    ? request.getVersion().trim()
                    : "1.0";
            String moduleCode = request.getModuleCode() != null ? request.getModuleCode().trim() : "GENERAL";
            String fileType = request.getFileType() != null ? request.getFileType().trim() : "txt";
            String status = request.getStatus() != null ? request.getStatus().trim() : "PROCESADO";

            VectorialDocumentEntity firstEntity = null;

            for (String chunk : chunks) {
                VectorialDocumentEntity entity = new VectorialDocumentEntity();
                entity.setDocumentTitle(request.getDocumentTitle().trim());
                entity.setChunkContent(chunk);
                entity.setModuleCode(moduleCode);
                entity.setVersion(version);
                entity.setStatus(status);
                entity.setMetadata(request.getMetadata());
                entity.setFileType(fileType);
                entity.setFileSize(request.getFileSize() != null ? request.getFileSize() : (long) chunk.length());
                entity.setActive(true);

                VectorialDocumentEntity saved = repository.save(entity);
                if (firstEntity == null) {
                    firstEntity = saved;
                }
            }

            VectorialDocumentDTO resultDTO = mapToDTO(firstEntity, chunks.size());
            return ResponseEntity.ok(APIResponseDTO.success(resultDTO, "Documento vectorial agregado exitosamente"));

        } catch (Exception e) {
            logger.error("Error al crear documento RAG", e);
            return ResponseEntity.internalServerError().body(
                    APIResponseDTO.failure("Error al crear el documento vectorial: " + e.getMessage(), "RAG_CREATE_ERROR")
            );
        }
    }

    @Override
    @Transactional
    public ResponseEntity<APIResponseDTO<VectorialDocumentDTO>> replaceDocument(Long id, VectorialDocumentRequestDTO request) {
        try {
            Optional<VectorialDocumentEntity> existingOpt = repository.findById(id);
            String titleToUpdate = request.getDocumentTitle();

            if (existingOpt.isPresent()) {
                VectorialDocumentEntity existing = existingOpt.get();
                if (titleToUpdate == null || titleToUpdate.trim().isEmpty()) {
                    titleToUpdate = existing.getDocumentTitle();
                }
                // Desactivar / Marcar versiones anteriores del documento
                repository.deactivatePreviousVersions(existing.getDocumentTitle());
            } else if (titleToUpdate != null && !titleToUpdate.trim().isEmpty()) {
                repository.deactivatePreviousVersions(titleToUpdate.trim());
            } else {
                return ResponseEntity.badRequest().body(
                        APIResponseDTO.failure("No se encontró el documento a reemplazar", "NOT_FOUND")
                );
            }

            List<String> chunks = prepareChunks(request);
            if (chunks.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        APIResponseDTO.failure("El contenido de la nueva versión no puede estar vacío", "EMPTY_CONTENT")
                );
            }

            String version = request.getVersion() != null && !request.getVersion().trim().isEmpty()
                    ? request.getVersion().trim()
                    : "2.0";
            String moduleCode = request.getModuleCode() != null ? request.getModuleCode().trim() : "GENERAL";
            String fileType = request.getFileType() != null ? request.getFileType().trim() : "txt";

            VectorialDocumentEntity firstEntity = null;

            for (String chunk : chunks) {
                VectorialDocumentEntity newEntity = new VectorialDocumentEntity();
                newEntity.setDocumentTitle(titleToUpdate.trim());
                newEntity.setChunkContent(chunk);
                newEntity.setModuleCode(moduleCode);
                newEntity.setVersion(version);
                newEntity.setStatus("PROCESADO");
                newEntity.setMetadata(request.getMetadata());
                newEntity.setFileType(fileType);
                newEntity.setFileSize(request.getFileSize() != null ? request.getFileSize() : (long) chunk.length());
                newEntity.setActive(true);

                VectorialDocumentEntity saved = repository.save(newEntity);
                if (firstEntity == null) {
                    firstEntity = saved;
                }
            }

            VectorialDocumentDTO resultDTO = mapToDTO(firstEntity, chunks.size());
            return ResponseEntity.ok(APIResponseDTO.success(resultDTO, "Nueva versión del documento registrada exitosamente"));

        } catch (Exception e) {
            logger.error("Error al reemplazar/versionar documento RAG", e);
            return ResponseEntity.internalServerError().body(
                    APIResponseDTO.failure("Error al versionar documento: " + e.getMessage(), "RAG_VERSION_ERROR")
            );
        }
    }

    @Override
    @Transactional
    public ResponseEntity<APIResponseDTO<String>> deleteDocument(Long id) {
        try {
            Optional<VectorialDocumentEntity> opt = repository.findById(id);
            if (opt.isPresent()) {
                VectorialDocumentEntity entity = opt.get();
                repository.deleteByDocumentTitle(entity.getDocumentTitle());
                return ResponseEntity.ok(APIResponseDTO.success("Documento y fragmentos eliminados correctamente", "Documento eliminado"));
            } else {
                return ResponseEntity.badRequest().body(
                        APIResponseDTO.failure("El documento especificado no existe", "NOT_FOUND")
                );
            }
        } catch (Exception e) {
            logger.error("Error al eliminar documento RAG", e);
            return ResponseEntity.internalServerError().body(
                    APIResponseDTO.failure("Error al eliminar el documento: " + e.getMessage(), "RAG_DELETE_ERROR")
            );
        }
    }

    @Override
    public ResponseEntity<APIResponseDTO<List<VectorialDocumentDTO>>> getChunksByDocumentTitle(String title) {
        try {
            List<VectorialDocumentEntity> entities = repository.findByDocumentTitleAndActiveTrue(title);
            List<VectorialDocumentDTO> dtos = entities.stream()
                    .map(e -> mapToDTO(e, 1))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(APIResponseDTO.success(dtos, "Fragmentos obtenidos exitosamente"));
        } catch (Exception e) {
            logger.error("Error al obtener fragmentos del documento", e);
            return ResponseEntity.internalServerError().body(
                    APIResponseDTO.failure("Error al obtener fragmentos: " + e.getMessage(), "RAG_CHUNKS_ERROR")
            );
        }
    }

    private List<String> prepareChunks(VectorialDocumentRequestDTO request) {
        List<String> chunks = new ArrayList<>();
        if (request.getChunks() != null && !request.getChunks().isEmpty()) {
            for (String ch : request.getChunks()) {
                if (ch != null && !ch.trim().isEmpty()) {
                    chunks.add(ch.trim());
                }
            }
        } else if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            String text = request.getContent().trim();
            int chunkSize = 800;
            int overlap = 100;
            int start = 0;

            while (start < text.length()) {
                int end = Math.min(start + chunkSize, text.length());
                String chunk = text.substring(start, end);
                chunks.add(chunk);
                if (end == text.length()) break;
                start += (chunkSize - overlap);
            }
        }
        return chunks;
    }

    private VectorialDocumentDTO mapToDTO(VectorialDocumentEntity entity, int chunkCount) {
        if (entity == null) return null;
        return VectorialDocumentDTO.builder()
                .id(entity.getId())
                .documentTitle(entity.getDocumentTitle())
                .chunkContent(entity.getChunkContent())
                .moduleCode(entity.getModuleCode())
                .vectorEmbedding(entity.getVectorEmbedding())
                .version(entity.getVersion())
                .status(entity.getStatus())
                .metadata(entity.getMetadata())
                .fileType(entity.getFileType())
                .fileSize(entity.getFileSize())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .chunkCount(chunkCount)
                .build();
    }
}
