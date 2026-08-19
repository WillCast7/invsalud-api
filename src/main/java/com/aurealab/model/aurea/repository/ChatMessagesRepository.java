package com.aurealab.model.aurea.repository;

import com.aurealab.model.aurea.entity.ChatMessagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessagesRepository extends JpaRepository<ChatMessagesEntity, Long> {
    
    /**
     * Obtiene los mensajes de una sesión ordenados cronológicamente
     */
    List<ChatMessagesEntity> findBySessionIdOrderByCreatedAtAsc(String sessionId);
}
