package com.aurealab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VectorialDocumentRequestDTO implements Serializable {
    private Long id;
    private String documentTitle;
    private String content;
    private String moduleCode;
    private String version;
    private String status;
    private String metadata;
    private String fileType;
    private Long fileSize;
    private List<String> chunks;
}
