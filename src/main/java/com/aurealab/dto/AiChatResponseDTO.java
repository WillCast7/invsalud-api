package com.aurealab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatResponseDTO implements Serializable {

    private String sessionId;
    private String rol;       // 'assistant'
    private String content;   // Respuesta generada por la IA
    private String createdAt; // ISO Timestamp de creación
    private List<Map<String, Object>> sources; // Citas/Fuentes RAG de la BD
}
