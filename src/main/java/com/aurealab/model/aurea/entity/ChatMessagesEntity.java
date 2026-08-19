package com.aurealab.model.aurea.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_session_chat", columnList = "session_id, created_at")
})
public class ChatMessagesEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "session_id", nullable = false)
        private String sessionId;

        @Column(nullable = false, length = 50)
        private String rol; // 'user', 'assistant', 'tool'

        @Column(nullable = false, columnDefinition = "TEXT")
        private String content;

        @Column(name = "created_at", updatable = false)
        private LocalDateTime createdAt = LocalDateTime.now();

        // Constructores
        public ChatMessagesEntity() {}

        public ChatMessagesEntity(String sessionId, String rol, String content) {
                this.sessionId = sessionId;
                this.rol = rol;
                this.content = content;
        }

        // Getters y Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }

        public String getRol() { return rol; }
        public void setRol(String rol) { this.rol = rol; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
