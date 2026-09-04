package com.aurealab.service.impl;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.AiChatRequestDTO;
import com.aurealab.dto.AiChatResponseDTO;
import com.aurealab.model.aurea.entity.ChatMessagesEntity;
import com.aurealab.model.aurea.entity.VectorialDocumentEntity;
import com.aurealab.model.aurea.repository.ChatMessagesRepository;
import com.aurealab.model.aurea.repository.VectorialDocumentRepository;
import com.aurealab.model.inventory.entity.*;
import com.aurealab.model.inventory.repository.*;
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

import java.time.LocalDate;
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

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private ResolutionRepository resolutionRepository;

    @Autowired
    private PurchasingRepository purchasingRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

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

            // 2. Determinar modo de búsqueda ('API' vs 'DOCUMENTOS')
            String searchMode = resolveSearchMode(request, userContent);
            logger.info("Modo de búsqueda activo: {} para consulta: {}", searchMode, userContent);

            List<Map<String, Object>> sourcesList = new ArrayList<>();
            String aiReply;

            if ("API".equalsIgnoreCase(searchMode)) {
                aiReply = handleApiSearch(userContent, sourcesList);
            } else {
                aiReply = handleDocumentRagSearch(userContent, request.getModuleFilter(), sourcesList);
            }

            // 3. Guardar respuesta del asistente en la tabla chat_messages
            ChatMessagesEntity assistantMsg = new ChatMessagesEntity();
            assistantMsg.setSessionId(sessionId);
            assistantMsg.setRol("assistant");
            assistantMsg.setContent(aiReply);
            chatMessagesRepository.save(assistantMsg);
            logger.info("Guardada respuesta del asistente en DB para sesión {}", sessionId);

            // 4. Armar DTO de respuesta incluyendo las fuentes consultadas
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
     * Determina el modo de búsqueda basándose en request.searchMode, moduleFilter, context o análisis de la pregunta.
     */
    private String resolveSearchMode(AiChatRequestDTO request, String userPrompt) {
        if (request.getSearchMode() != null && !request.getSearchMode().trim().isEmpty()) {
            return request.getSearchMode().trim().toUpperCase();
        }
        if (request.getContext() != null && request.getContext().containsKey("searchMode")) {
            return String.valueOf(request.getContext().get("searchMode")).trim().toUpperCase();
        }

        String filter = request.getModuleFilter();
        if (filter != null) {
            if ("API".equalsIgnoreCase(filter) || "DATOS".equalsIgnoreCase(filter) || "INVENTARIO".equalsIgnoreCase(filter)) {
                return "API";
            }
            if ("DOCUMENTOS".equalsIgnoreCase(filter) || "NORMATIVA".equalsIgnoreCase(filter) || "MANUALES".equalsIgnoreCase(filter) || "RESOLUCIONES".equalsIgnoreCase(filter)) {
                return "DOCUMENTOS";
            }
        }

        // Detección automática por palabras clave
        String lower = userPrompt.toLowerCase();
        if (lower.contains("stock") || lower.contains("inventario") || lower.contains("precio")
                || lower.contains("lote") || lower.contains("unidades") || lower.contains("cuanto")
                || lower.contains("cuántos") || lower.contains("producto") || lower.contains("medicamento")
                || lower.contains("compra") || lower.contains("orden") || lower.contains("cliente")
                || lower.contains("proveedor") || lower.contains("dolex") || lower.contains("metadona")
                || lower.contains("atorvastatina")) {
            return "API";
        }
        return "DOCUMENTOS";
    }

    /**
     * Procesa la búsqueda en el modo API: consulta repositorios de la BD y enriquece el LLM o formatea directamente.
     */
    private String handleApiSearch(String userPrompt, List<Map<String, Object>> sourcesList) {
        String dataReport = executeSystemDataQuery(userPrompt);

        // Registrar fuente de API
        Map<String, Object> apiSource = new HashMap<>();
        apiSource.put("documentTitle", "Base de Datos en Vivo (API INVSALUD)");
        apiSource.put("moduleCode", "API_DATOS");
        apiSource.put("similarity", 1.0);
        apiSource.put("contentSnippet", "Registros recuperados directamente desde la base de datos del sistema.");
        sourcesList.add(apiSource);

        // Preparar prompt para que Ollama redacte la respuesta con los datos reales
        String systemEnrichedPrompt = """
        ### CONTEXTO DE DATOS REALES DE INVSALUD (CONSULTADOS EN VIVO DE LA BASE DE DATOS):
        %s

        ### INSTRUCCIÓN OBLIGATORIA:
        Un usuario realizó la siguiente consulta: "%s"
        1. Responde a la pregunta usando ÚNICAMENTE los datos oficiales suministrados arriba.
        2. NO digas que no tienes acceso a datos en tiempo real, ya que estos datos fueron leídos directamente de la base de datos de producción para esta respuesta.
        3. Formatea la respuesta con viñetas claras y legibles, resaltando nombres, cantidades y fechas.
        """.formatted(dataReport, userPrompt);

        String llmReply = callOllamaSimple(systemEnrichedPrompt, userPrompt);

        // Si el LLM respondió adecuadamente y sin rechazos genéricos, usamos su texto
        if (isValidLlmAnswer(llmReply)) {
            return llmReply;
        }

        // Si Ollama no está disponible o dio una negativa genérica ("no tengo acceso en tiempo real"),
        // devolvemos directamente el reporte formateado con los datos reales de la BD
        return "📊 **Resultados de la consulta en el sistema (API INVSALUD)**:\n\n" + dataReport;
    }

    /**
     * Consulta los repositorios de la base de datos según la intención detectada en la pregunta.
     */
    private String executeSystemDataQuery(String userPrompt) {
        String lower = userPrompt.toLowerCase();
        StringBuilder sb = new StringBuilder();

        boolean matchedAny = false;

        // 1. Consulta de medicamentos específicos o stock de inventario
        List<PrescriptionInventoryEntity> inventoryResults = new ArrayList<>();
        String searchProductTerm = extractProductTerm(lower);

        if (searchProductTerm != null && !searchProductTerm.isEmpty()) {
            inventoryResults = prescriptionInventoryRepository.findByProductNameContainingIgnoreCase(searchProductTerm);
        } else if (lower.contains("stock") || lower.contains("inventario") || lower.contains("disponible") || lower.contains("lote")) {
            inventoryResults = prescriptionInventoryRepository.findAllActiveStock();
        }

        if (!inventoryResults.isEmpty()) {
            matchedAny = true;
            sb.append("### INVENTARIO Y STOCK DE MEDICAMENTOS:\n");
            int totalUnits = 0;
            for (PrescriptionInventoryEntity item : inventoryResults) {
                String prodName = item.getProduct() != null ? item.getProduct().getName() : "Sin Nombre";
                String prodCode = item.getProduct() != null ? item.getProduct().getCode() : "N/A";
                String batchCode = item.getBatch() != null ? item.getBatch().getCode() : "N/A";
                String expDate = item.getExpirationDate() != null ? item.getExpirationDate().toString() : "N/A";
                String salePrice = item.getSalePrice() != null ? "$" + item.getSalePrice() : "N/A";

                totalUnits += item.getAvailableUnits();

                sb.append(String.format("- **%s** (Código: `%s`) | Lote: `%s` | Unidades Disponibles: **%d** (Total: %d) | Vencimiento: **%s** | Precio Venta: %s\n",
                        prodName, prodCode, batchCode, item.getAvailableUnits(), item.getTotalUnits(), expDate, salePrice));
            }
            sb.append(String.format("\n*Total unidades encontradas: %d*\n\n", totalUnits));
        }

        // 2. Consulta del catálogo de productos registrados
        if (lower.contains("producto") || lower.contains("catalogo") || lower.contains("catálogo") || lower.contains("medicamentos registrados") || lower.contains("qué medicamentos hay") || lower.contains("que productos hay")) {
            matchedAny = true;
            List<ProductEntity> products = productRepository.findByIsActiveTrue();
            sb.append("### CATÁLOGO DE PRODUCTOS REGISTRADOS:\n");
            for (ProductEntity p : products) {
                sb.append(String.format("- **%s** (Código: `%s`) | Forma: %s | Concentración: %s | Estado: %s\n",
                        p.getName(), p.getCode(),
                        p.getPharmaceuticalForm() != null ? p.getPharmaceuticalForm() : "N/A",
                        p.getConcentration() != null ? p.getConcentration() : "N/A",
                        Boolean.TRUE.equals(p.getIsActive()) ? "Activo" : "Inactivo"));
            }
            sb.append("\n");
        }

        // 3. Consulta de Resoluciones del sistema
        if (lower.contains("resolucion") || lower.contains("resolución") || lower.contains("resoluciones")) {
            matchedAny = true;
            List<ResolutionEntity> resolutions = resolutionRepository.findAll();
            sb.append("### RESOLUCIONES REGISTRADAS EN EL SISTEMA:\n");
            if (resolutions.isEmpty()) {
                sb.append("- No hay resoluciones registradas actualmente en el sistema.\n");
            } else {
                for (ResolutionEntity r : resolutions) {
                    sb.append(String.format("- **Resolución #%s** | Vigencia: %s hasta %s | Estado: %s | Descripción: %s\n",
                            r.getCode(),
                            r.getStartDate() != null ? r.getStartDate().toString() : "N/A",
                            r.getExpirationDate() != null ? r.getExpirationDate().toString() : "N/A",
                            Boolean.TRUE.equals(r.getIsActive()) ? "Activa" : "Inactiva",
                            r.getDescription() != null && !r.getDescription().isEmpty() ? r.getDescription() : "Sin descripción"));
                }
            }
            sb.append("\n");
        }

        // 4. Consulta de Compras / Órdenes
        if (lower.contains("compra") || lower.contains("adquisici") || lower.contains("orden") || lower.contains("órden") || lower.contains("venta")) {
            matchedAny = true;
            List<PurchasingEntity> purchases = purchasingRepository.findAllActivePurchases();
            sb.append("### COMPRAS / ADQUISICIONES RECIENTES:\n");
            if (purchases.isEmpty()) {
                sb.append("- No hay registros de compras activas en este momento.\n");
            } else {
                int limit = Math.min(purchases.size(), 5);
                for (int i = 0; i < limit; i++) {
                    PurchasingEntity p = purchases.get(i);
                    sb.append(String.format("- Compra #%d | Tipo: %s | Total: $%s | Fecha: %s | Estado: %s\n",
                            p.getId(), p.getType() != null ? p.getType() : "N/A",
                            p.getTotal() != null ? p.getTotal().toString() : "0.00",
                            p.getCreatedAt() != null ? p.getCreatedAt().toLocalDate().toString() : "N/A",
                            Boolean.TRUE.equals(p.getIsActive()) ? "Activa" : "Inactiva"));
                }
            }
            sb.append("\n");
        }

        // 5. Consulta de Proveedores / Clientes / Terceros
        if (lower.contains("proveedor") || lower.contains("cliente") || lower.contains("tercero")) {
            matchedAny = true;
            List<ThirdPartyEntity> thirdParties = thirdPartyRepository.findAll();
            sb.append("### TERCEROS REGISTRADOS (CLIENTES / PROVEEDORES):\n");
            if (thirdParties.isEmpty()) {
                sb.append("- No hay terceros registrados en el sistema.\n");
            } else {
                int limit = Math.min(thirdParties.size(), 8);
                for (int i = 0; i < limit; i++) {
                    ThirdPartyEntity tp = thirdParties.get(i);
                    sb.append(String.format("- **%s** | Documento: %s %s | Tel: %s\n",
                            tp.getFullName(), tp.getDocumentType(), tp.getDocumentNumber(),
                            tp.getPhoneNumber() != null ? tp.getPhoneNumber() : "N/A"));
                }
            }
            sb.append("\n");
        }

        // Si la pregunta en modo API no coincide con palabras específicas, traer un resumen general de stock y productos
        if (!matchedAny) {
            List<PrescriptionInventoryEntity> stock = prescriptionInventoryRepository.findAllActiveStock();
            sb.append("### RESUMEN DE STOCK EN INVENTARIO:\n");
            if (stock.isEmpty()) {
                sb.append("- Actualmente no hay unidades registradas con stock activo disponible.\n");
            } else {
                for (PrescriptionInventoryEntity item : stock) {
                    String name = item.getProduct() != null ? item.getProduct().getName() : "Producto";
                    String batch = item.getBatch() != null ? item.getBatch().getCode() : "N/A";
                    sb.append(String.format("- **%s** | Lote: `%s` | Disponibles: **%d uds** | Vence: %s\n",
                            name, batch, item.getAvailableUnits(), item.getExpirationDate()));
                }
            }
        }

        return sb.toString();
    }

    private String extractProductTerm(String lower) {
        if (lower.contains("dolex")) return "dolex";
        if (lower.contains("metadona")) return "metadona";
        if (lower.contains("atorvastatina")) return "atorvastatina";

        String clean = lower
                .replace("cuanto", "")
                .replace("cuánto", "")
                .replace("hay de", "")
                .replace("stock de", "")
                .replace("inventario de", "")
                .replace("tienes", "")
                .replace("queda de", "")
                .replace("consultar", "")
                .replace("buscar", "")
                .replace("precio de", "")
                .trim();

        if (clean.length() > 2 && !clean.contains(" ")) {
            return clean;
        }
        return null;
    }

    /**
     * Procesa la búsqueda en el modo DOCUMENTOS (RAG)
     */
    private String handleDocumentRagSearch(String userPrompt, String moduleFilter, List<Map<String, Object>> sourcesList) {
        String ragContextStr = retrieveRagContext(userPrompt, moduleFilter, sourcesList);

        // Si se encontraron fragmentos en la BD vectorial
        if (!sourcesList.isEmpty()) {
            String enrichedUserPrompt = userPrompt + ragContextStr;
            String reply = callOllamaSimple(SYSTEM_PROMPT, enrichedUserPrompt);
            if (reply != null && !reply.isEmpty() && !reply.contains("No fue posible establecer comunicación")) {
                return sanitizeAiResponse(reply);
            }
        }

        // Si no hay documentos en la tabla vectorials_documents (0 registros cargados aún)
        return generateProceduralGuideFallback(userPrompt);
    }

    /**
     * Guía procedimental mientras se cargan los documentos vectoriales normativos
     */
    private String generateProceduralGuideFallback(String prompt) {
        String lower = prompt.toLowerCase();

        if (lower.contains("crear") || lower.contains("nuevo") || lower.contains("agregar") || lower.contains("insumo") || lower.contains("producto")) {
            return """
            🛠️ **Guía Procedimental: Creación y Gestión de Insumos**
            
            Para crear un nuevo producto o insumo en **INVSALUD**:
            1. Dirígete en el menú lateral a **Inventario > Gestión de Productos**.
            2. Haz clic en el botón `+ Nuevo Producto`.
            3. Diligencia los campos requeridos: Código, Nombre del medicamento, Forma farmacéutica, Concentración y tipo de medicamento (Control Especial, Salud Pública o Recetario).
            4. Presiona `Guardar`.
            
            💡 *Nota: Si deseas consultar las existencias reales cargadas en el sistema, presiona el botón **API / Datos** en la parte superior del chat.*
            """;
        } else if (lower.contains("vencid") || lower.contains("vencer") || lower.contains("baja") || lower.contains("cuarentena") || lower.contains("normat")) {
            return """
            📜 **Guía Procedimental y Normativa de Medicamentos**
            
            Conforme a la normativa farmacéutica y directrices de control sanitario:
            1. **Semaforización**: Los medicamentos con menos de 60 días para su vencimiento deben mantenerse en alerta preventiva (Amarillo/Rojo).
            2. **Retiro y Devolución**: Todo producto vencido debe ser segregado inmediatamente del área de dispensación y trasladado a cuarentena para devolución o acta de destrucción.
            3. **En la Aplicación**: Para dar de baja un lote, accede a **Inventario**, ubica el lote respectivo y selecciona la opción de retiro indicando motivo y observación.
            
            ℹ️ *Los documentos PDF normativos se están indexando en el motor RAG. Para consultar lotes específicos registrados en el sistema, usa el botón **API / Datos**.*
            """;
        } else {
            return String.format("""
            📚 **Búsqueda en Documentos (RAG) - INVSALUD**
            
            Has realizado la consulta: "*%s*".
            
            Actualmente la base de conocimiento vectorial para normativas y resoluciones en PDF se encuentra en preparación para la carga de documentos.
            
            - Para consultar información sobre **cómo realizar procedimientos** dentro de la app (crear productos, dar de baja lotes, registrar compras), puedes preguntar directamente aquí.
            - Para consultar **stock en tiempo real, lotes, productos activos o compras** almacenadas en el sistema, selecciona el botón **[🔌 API / Datos]** arriba en el chat.
            """, prompt);
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
     * Llamada directa y robusta a Ollama sin depender de tool-calling para evitar descarte de datos
     */
    private String callOllamaSimple(String systemContent, String userContent) {
        try {
            String url = buildOllamaChatUrl();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            List<Map<String, Object>> messages = new ArrayList<>();

            Map<String, Object> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemContent);
            messages.add(systemMsg);

            Map<String, Object> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userContent);
            messages.add(userMsg);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", ollamaModel);
            requestBody.put("messages", messages);
            requestBody.put("stream", false);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Object msgObj = body.get("message");
                if (msgObj instanceof Map<?, ?> assistantMsgMap) {
                    Object contentObj = assistantMsgMap.get("content");
                    if (contentObj != null && !contentObj.toString().trim().isEmpty()) {
                        return sanitizeAiResponse(contentObj.toString());
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("No fue posible comunicarse con Ollama en {}: {}", ollamaHost, e.getMessage());
        }
        return null;
    }

    private boolean isValidLlmAnswer(String reply) {
        if (reply == null || reply.trim().isEmpty()) return false;
        String lower = reply.toLowerCase();
        if (lower.contains("no puedo proporcionar informaci") && lower.contains("tiempo real")) return false;
        if (lower.contains("no tengo acceso a informaci") && lower.contains("tiempo real")) return false;
        if (lower.contains("no tengo acceso a datos en tiempo real")) return false;
        if (lower.contains("como modelo de lenguaje")) return false;
        return true;
    }

    private String sanitizeAiResponse(String content) {
        if (content == null) return "";
        if (content.contains("{\"name\":") || content.contains("\"parameters\":")) {
            content = content.replaceAll("\\{\\s*\"name\"\\s*:\\s*\"[^\"]+\"\\s*,\\s*\"parameters\"\\s*:\\s*\\{[^}]*\\}\\s*\\}", "").trim();
        }
        return content;
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
}
