package com.aurealab.service;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.AiChatRequestDTO;
import com.aurealab.dto.AiChatResponseDTO;
import com.aurealab.model.aurea.entity.ChatMessagesEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AiChatService {

    /**
     * Procesa un mensaje de usuario: lo guarda en DB, consulta a Ollama,
     * guarda la respuesta de la IA en DB y retorna la respuesta.
     */
    ResponseEntity<APIResponseDTO<AiChatResponseDTO>> processChat(AiChatRequestDTO request);

    /**
     * Obtiene los mensajes anteriores de una sesión de chat almacenados en la tabla chat_messages
     */
    ResponseEntity<APIResponseDTO<List<ChatMessagesEntity>>> getChatHistory(String sessionId);
}
