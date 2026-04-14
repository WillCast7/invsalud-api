package com.aurealab.service.impl.inventory;

import com.aurealab.model.inventory.entity.DocumentSequenceEntity;
import com.aurealab.model.inventory.repository.DocumentSequenceRepository;
import com.aurealab.service.Inventory.DocumentSequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentSequenceServiceImpl implements DocumentSequenceService {

    @Autowired
    DocumentSequenceRepository documentSequenceRepository;

    public String getNextInvoiceNumber(String prefix) {

        DocumentSequenceEntity sequence = documentSequenceRepository.findByPrefix(prefix);

        if (sequence == null) {
            sequence = new DocumentSequenceEntity();
            sequence.setPrefix(prefix);
            sequence.setLastValue(0L);
            // Opcional: setTenantId si es necesario en la entidad
        }

        // 3. Incrementamos
        sequence.setLastValue(sequence.getLastValue() + 1);

        // 4. Guardamos dentro del mismo contexto
        DocumentSequenceEntity saved = documentSequenceRepository.save(sequence);

        // 5. Retornamos con formato (ej: FE-01)
        return String.format("%s-%02d", saved.getPrefix(), saved.getLastValue());
    }
}
