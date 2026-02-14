package com.aurealab.service.impl.CashRegister;

import com.aurealab.model.cashRegister.entity.DocumentSequenceEntity;
import com.aurealab.model.cashRegister.repository.DocumentSequenceRepository;
import com.aurealab.service.impl.shared.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentSequenceServiceImpl {
    @Autowired
    DocumentSequenceRepository documentSequenceRepository;

    @Autowired
    TenantService tenantService;

    private String tenancy = "conduvalle";

    public String getNextInvoiceNumber(String prefix) {
        // 1. Todo ocurre en una sola transacción y bajo el mismo tenant
        return tenantService.executeInTenant(tenancy, () -> {

            // 2. Buscamos el registro. Si no existe, lo creamos con valor 0
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
        });
    }
}
