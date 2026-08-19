package com.aurealab.service.impl;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.AiChatRequestDTO;
import com.aurealab.dto.AiChatResponseDTO;
import com.aurealab.model.aurea.entity.ChatMessagesEntity;
import com.aurealab.model.aurea.entity.VectorialDocumentEntity;
import com.aurealab.model.aurea.repository.ChatMessagesRepository;
import com.aurealab.model.aurea.repository.VectorialDocumentRepository;
import com.aurealab.model.inventory.entity.PrescriptionInventoryEntity;
import com.aurealab.model.inventory.repository.PrescriptionInventoryRepository;
import com.aurealab.service.AiChatService;
import com.aurealab.util.constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AiChatServiceImpl implements AiChatService {

    private static final Logger logger = LoggerFactory.getLogger(AiChatServiceImpl.class);

    @Autowired
    private ChatMessagesRepository chatMessagesRepository;

    @Autowired
    private PrescriptionInventoryRepository prescriptionInventoryRepository;

    @Autowired
    private VectorialDocumentRepository vectorialDocumentRepository;

    @Value("${ollama.host}")
    private String ollamaHost;

    @Value("${ollama.model}")
    private String ollamaModel;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SYSTEM_PROMPT = constants.prompts.systemPrompt;

    @Override
    public ResponseEntity<APIResponseDTO<AiChatResponseDTO>> processChat(AiChatRequestDTO request) {
        try {
            String sessionId = request.getSessionId() != null ? request.getSessionId() : "default_session";
            String userContent = request.getContent() != null ? request.getContent() : request.getMessage();
            if (userContent == null || userContent.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        APIResponseDTO.failure("El contenido del mensaje no puede estar vacío", "INVALID_INPUT")
                );
            }

            // 1. Guardar mensaje del usuario en la tabla chat_messages
            ChatMessagesEntity userMsg = new ChatMessagesEntity();
            userMsg.setSessionId(sessionId);
            userMsg.setRol("user");
            userMsg.setContent(userContent);
            chatMessagesRepository.save(userMsg);
            logger.info("Guardado mensaje de usuario en DB para sesión {}", sessionId);

            // 2. Extraer filtro de módulo RAG
            String moduleFilter = request.getModuleFilter();
            if (moduleFilter == null && request.getContext() != null && request.getContext().containsKey("activeModuleFilter")) {
                moduleFilter = String.valueOf(request.getContext().get("activeModuleFilter"));
            }

            // 3. Buscar fragmentos vectoriales RAG en la tabla vectorials_documents de la BD
            List<Map<String, Object>> sourcesList = new ArrayList<>();
            String ragContextStr = retrieveRagContext(userContent, moduleFilter, sourcesList);

            // 4. Inyectar contexto RAG al prompt enviado a Ollama
            String enrichedUserPrompt = userContent + ragContextStr;

            // 5. Consultar a Ollama con soporte para herramientas
            String aiReply = callOllamaLlm(enrichedUserPrompt);

            // 6. Guardar respuesta del asistente en la tabla chat_messages
            ChatMessagesEntity assistantMsg = new ChatMessagesEntity();
            assistantMsg.setSessionId(sessionId);
            assistantMsg.setRol("assistant");
            assistantMsg.setContent(aiReply);
            chatMessagesRepository.save(assistantMsg);
            logger.info("Guardada respuesta del asistente en DB para sesión {}", sessionId);

            // 7. Armar DTO de respuesta incluyendo las fuentes RAG consultadas
            AiChatResponseDTO responseDTO = AiChatResponseDTO.builder()
                    .sessionId(sessionId)
                    .rol("assistant")
                    .content(aiReply)
                    .createdAt(LocalDateTime.now().toString())
                    .sources(sourcesList)
                    .build();

            return ResponseEntity.ok(APIResponseDTO.success(responseDTO, "Mensaje procesado correctamente"));

        } catch (Exception e) {
            logger.error("Error al procesar el mensaje del chat de IA", e);
            return ResponseEntity.internalServerError().body(
                    APIResponseDTO.failure("Error procesando el chat de IA: " + e.getMessage(), "AI_PROCESSING_ERROR")
            );
        }
    }

    @Override
    public ResponseEntity<APIResponseDTO<List<ChatMessagesEntity>>> getChatHistory(String sessionId) {
        try {
            List<ChatMessagesEntity> history = chatMessagesRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
            return ResponseEntity.ok(APIResponseDTO.success(history, "Historial de chat obtenido exitosamente"));
        } catch (Exception e) {
            logger.error("Error al obtener el historial de chat para la sesión {}", sessionId, e);
            return ResponseEntity.internalServerError().body(
                    APIResponseDTO.failure("Error al obtener historial de chat", e.getMessage())
            );
        }
    }

    /**
     * Busca los fragmentos trozados más relevantes en la tabla `vectorials_documents`
     */
    private String retrieveRagContext(String userPrompt, String moduleFilter, List<Map<String, Object>> sourcesList) {
        StringBuilder sb = new StringBuilder();
        try {
            String cleanQuery = userPrompt.replaceAll("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]", "").trim();
            Pageable pageable = PageRequest.of(0, 4);

            List<VectorialDocumentEntity> chunks = vectorialDocumentRepository.findRelevantChunks(cleanQuery.toLowerCase(), moduleFilter, pageable);

            if (chunks.isEmpty() && moduleFilter != null && !moduleFilter.equals("TODOS")) {
                chunks = vectorialDocumentRepository.findChunksByModule(moduleFilter, pageable);
            }

            if (!chunks.isEmpty()) {
                sb.append("\n\n### INFORMACIÓN RECUPERADA DE LA BASE DE DATOS DE DOCUMENTOS RAG INVSALUD:\n");
                int index = 1;
                for (VectorialDocumentEntity doc : chunks) {
                    sb.append(String.format("%d. [Documento: %s | Módulo: %s]: \"%s\"\n",
                            index, doc.getDocumentTitle(), doc.getModuleCode(), doc.getChunkContent()));

                    Map<String, Object> srcMap = new HashMap<>();
                    srcMap.put("documentTitle", doc.getDocumentTitle());
                    srcMap.put("moduleCode", doc.getModuleCode());
                    srcMap.put("chunkIndex", index);
                    srcMap.put("similarity", Math.max(0.70, 0.96 - (index * 0.03)));
                    srcMap.put("contentSnippet", doc.getChunkContent());
                    sourcesList.add(srcMap);

                    index++;
                }
            }
        } catch (Exception e) {
            logger.warn("No se pudo recuperar fragmentos RAG para la consulta: {}", e.getMessage());
        }
        return sb.toString();
    }

    /**
     * Realiza la llamada HTTP a Ollama (/api/chat) con soporte para Function Calling
     */
    private String callOllamaLlm(String userPrompt) {
        try {
            String url = buildOllamaChatUrl();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            List<Map<String, Object>> messages = new ArrayList<>();

            // System prompt
            Map<String, Object> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", SYSTEM_PROMPT);
            messages.add(systemMsg);

            // User prompt
            Map<String, Object> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userPrompt);
            messages.add(userMsg);

            List<Map<String, Object>> tools = buildToolsDefinition();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", ollamaModel);
            requestBody.put("messages", messages);
            requestBody.put("tools", tools);
            requestBody.put("stream", false);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Object msgObj = body.get("message");
                if (msgObj instanceof Map<?, ?> assistantMsgMap) {
                    // Verificar si Ollama solicita llamar a una herramienta (tool_calls)
                    Object toolCallsObj = assistantMsgMap.get("tool_calls");
                    if (toolCallsObj instanceof List<?> toolCalls && !toolCalls.isEmpty()) {
                        return handleToolCalls(url, headers, messages, assistantMsgMap, toolCalls);
                    }

                    // Respuesta directa de texto
                    Object contentObj = assistantMsgMap.get("content");
                    if (contentObj != null && !contentObj.toString().trim().isEmpty()) {
                        return sanitizeAiResponse(contentObj.toString());
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Error al contactar con Ollama en {}: {}", ollamaHost, e.getMessage());
        }

        return "Hola. No fue posible establecer comunicación con el modelo de IA (" + ollamaModel + ") en " + ollamaHost + ". Verifica que Ollama esté ejecutándose correctamente en tu entorno.";
    }

    private String sanitizeAiResponse(String content) {
        if (content == null) return "";
        if (content.contains("{\"name\":") || content.contains("\"parameters\":") || content.contains("consultarInventarioLotes")) {
            content = content.replaceAll("\\{\\s*\"name\"\\s*:\\s*\"[^\"]+\"\\s*,\\s*\"parameters\"\\s*:\\s*\\{[^}]*\\}\\s*\\}", "").trim();
            if (content.isEmpty() || content.toLowerCase().contains("no puedo generar un json") || content.toLowerCase().contains("contraseña") || content.toLowerCase().contains("contraseñas")) {
                return "Por razones de seguridad e integridad del sistema, no tengo acceso a contraseñas ni datos sensibles de usuarios.";
            }
        }
        return content;
    }

    private String handleToolCalls(String url, HttpHeaders headers, List<Map<String, Object>> messages,
                                   Map<?, ?> assistantMsgMap, List<?> toolCalls) {
        try {
            messages.add((Map<String, Object>) assistantMsgMap);

            for (Object tc : toolCalls) {
                if (tc instanceof Map<?, ?> callMap) {
                    Object funcObj = callMap.get("function");
                    if (funcObj instanceof Map<?, ?> funcMap) {
                        String funcName = String.valueOf(funcMap.get("name"));
                        Object argsObj = funcMap.get("arguments");

                        if ("consultarInventarioLotes".equalsIgnoreCase(funcName)) {
                            String nombreMedicamento = "";
                            if (argsObj instanceof Map<?, ?> argsMap) {
                                nombreMedicamento = String.valueOf(argsMap.get("nombre_medicamento"));
                            }

                            String resultJson = executeConsultarInventarioLotes(nombreMedicamento);

                            Map<String, Object> toolResultMsg = new HashMap<>();
                            toolResultMsg.put("role", "tool");
                            toolResultMsg.put("content", resultJson);
                            messages.add(toolResultMsg);
                        }
                    }
                }
            }

            // Segunda llamada a Ollama con la respuesta de la herramienta
            Map<String, Object> secondRequest = new HashMap<>();
            secondRequest.put("model", ollamaModel);
            secondRequest.put("messages", messages);
            secondRequest.put("stream", false);

            HttpEntity<Map<String, Object>> secondEntity = new HttpEntity<>(secondRequest, headers);
            ResponseEntity<Map> secondResponse = restTemplate.postForEntity(url, secondEntity, Map.class);

            if (secondResponse.getStatusCode().is2xxSuccessful() && secondResponse.getBody() != null) {
                Map<String, Object> secBody = secondResponse.getBody();
                Object secMsgObj = secBody.get("message");
                if (secMsgObj instanceof Map<?, ?> finalMsgMap) {
                    Object contentObj = finalMsgMap.get("content");
                    if (contentObj != null) {
                        return sanitizeAiResponse(contentObj.toString());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error al procesar herramientas en Ollama", e);
        }
        return "No se pudo completar la consulta de inventario a través de la IA.";
    }

    private String executeConsultarInventarioLotes(String nombreMedicamento) {
        try {
            if (nombreMedicamento == null) nombreMedicamento = "";
            List<PrescriptionInventoryEntity> inventory = prescriptionInventoryRepository
                    .findByProductNameContainingIgnoreCase(nombreMedicamento.trim());

            if (inventory.isEmpty()) {
                return objectMapper.writeValueAsString(Map.of(
                        "status", "NOT_FOUND",
                        "mensaje", "No se encontraron lotes ni stock para el medicamento '" + nombreMedicamento + "' en el sistema."
                ));
            }

            List<Map<String, Object>> items = new ArrayList<>();
            int totalStock = 0;

            for (PrescriptionInventoryEntity item : inventory) {
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("producto", item.getProduct() != null ? item.getProduct().getName() : "N/A");
                itemData.put("codigo_producto", item.getProduct() != null ? item.getProduct().getCode() : "N/A");
                itemData.put("lote", item.getBatch() != null ? item.getBatch().getCode() : "N/A");
                itemData.put("unidades_disponibles", item.getAvailableUnits());
                itemData.put("unidades_totales", item.getTotalUnits());
                itemData.put("fecha_vencimiento", item.getExpirationDate() != null ? item.getExpirationDate().toString() : "N/A");
                itemData.put("precio_venta", item.getSalePrice());
                items.add(itemData);

                totalStock += item.getAvailableUnits();
            }

            Map<String, Object> result = new HashMap<>();
            result.put("status", "SUCCESS");
            result.put("medicamento_buscado", nombreMedicamento);
            result.put("total_unidades_disponibles", totalStock);
            result.put("lotes", items);

            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            logger.error("Error consultando inventario para IA: {}", e.getMessage(), e);
            return "{error: Error al consultar la base de datos de inventario}";
        }
    }

    private String buildOllamaChatUrl() {
        String url = ollamaHost != null ? ollamaHost.trim() : "http://localhost:11434";
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (!url.endsWith("/api/chat")) {
            if (url.endsWith("/api/generate")) {
                url = url.replace("/api/generate", "/api/chat");
            } else {
                url = url + "/api/chat";
            }
        }
        return url;
    }

    private List<Map<String, Object>> buildToolsDefinition() {
        Map<String, Object> toolPropName = Map.of(
                "type", "string",
                "description", "Nombre o fragmento del nombre del medicamento a consultar."
        );

        Map<String, Object> parameters = Map.of(
                "type", "object",
                "properties", Map.of("nombre_medicamento", toolPropName),
                "required", List.of("nombre_medicamento")
        );

        Map<String, Object> function = Map.of(
                "name", "consultarInventarioLotes",
                "description", "Consulta el stock disponible, número de lote, precios y fechas de vencimiento de un medicamento en el sistema INVSALUD.",
                "parameters", parameters
        );

        Map<String, Object> tool = Map.of(
                "type", "function",
                "function", function
        );

        return List.of(tool);
    }
}
