package com.aurealab.model.cashRegister.repository;

import com.aurealab.model.cashRegister.entity.DocumentSequenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentSequenceRepository extends JpaRepository<DocumentSequenceEntity, Long> {
    public DocumentSequenceEntity findByPrefix(String prefix);
}
