package com.aurealab.model.aurea.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "vectorials_documents")
public class VectorialDocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_title")
    private String documentTitle;

    @Column(name = "chunk_content", columnDefinition = "TEXT")
    private String chunkContent;

    @Column(name = "module_code")
    private String moduleCode;

    @Column(name = "vector_embedding", columnDefinition = "vector")
    private String vectorEmbedding;

    @Column(name = "version")
    private String version;

    @Column(name = "status")
    private String status;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.active == null) this.active = true;
        if (this.version == null) this.version = "1.0";
        if (this.status == null) this.status = "PROCESADO";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public VectorialDocumentEntity() {}

    public VectorialDocumentEntity(String documentTitle, String chunkContent, String vectorEmbedding) {
        this.documentTitle = documentTitle;
        this.chunkContent = chunkContent;
        this.vectorEmbedding = vectorEmbedding;
    }
}
