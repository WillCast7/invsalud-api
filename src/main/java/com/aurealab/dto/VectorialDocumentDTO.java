package com.aurealab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VectorialDocumentDTO implements Serializable {
    private Long id;
    private String documentTitle;
    private String chunkContent;
    private String moduleCode;
    private String vectorEmbedding;
    private String version;
    private String status;
    private String metadata;
    private String fileType;
    private Long fileSize;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer chunkCount;
}
