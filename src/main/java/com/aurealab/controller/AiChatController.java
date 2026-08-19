package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.AiChatRequestDTO;
import com.aurealab.dto.AiChatResponseDTO;
import com.aurealab.model.aurea.entity.ChatMessagesEntity;
import com.aurealab.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai/chat")
public class AiChatController {

    @Autowired
    AiChatService aiChatService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<APIResponseDTO<AiChatResponseDTO>> processChat(@RequestBody AiChatRequestDTO request) {
        return aiChatService.processChat(request);
    }

    @GetMapping(produces = "application/json", value = "/history")
    public ResponseEntity<APIResponseDTO<List<ChatMessagesEntity>>> getChatHistory(@RequestParam String sessionId) {
        return aiChatService.getChatHistory(sessionId);
    }
}
