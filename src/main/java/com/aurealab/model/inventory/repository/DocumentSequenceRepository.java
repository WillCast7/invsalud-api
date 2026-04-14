package com.aurealab.model.inventory.repository;

import com.aurealab.model.inventory.entity.DocumentSequenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentSequenceRepository extends JpaRepository<DocumentSequenceEntity, Long> {
    public DocumentSequenceEntity findByPrefix(String prefix);
}
