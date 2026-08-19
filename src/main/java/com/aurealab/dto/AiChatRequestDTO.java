package com.aurealab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequestDTO implements Serializable {

    private String sessionId;
    private String rol;          // 'user', 'assistant', 'tool'
    private String content;      // Texto del mensaje
    private String message;      // Alias alternativo para content
    private String moduleFilter; // Filtro RAG: 'NORMATIVA', 'MANUALES', 'INVENTARIO', 'RESOLUCIONES', etc.
    private Map<String, Object> context;
}
