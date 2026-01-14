package com.aurealab.service.impl.shared;

import com.aurealab.config.databases.multitenancy.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.function.Supplier;

@Service
public class TenantService {

    @Autowired
    private TransactionalExecutor transactionalExecutor;

    public <T> T executeInTenant(String tenantId, Supplier<T> operation) {
        String originalTenant = TenantContext.getCurrentTenant();

        try {
            // 1. Cambiamos el contexto PRIMERO (fuera de la transacción)
            TenantContext.setCurrentTenant(tenantId);
            System.out.println("📌 Contexto establecido para: " + tenantId);

            // 2. Ahora llamamos al ejecutor que INICIA la transacción
            // Hibernate llamará al Resolver y ahora sí leerá "conduvalle"
            return transactionalExecutor.execute(operation);

        } finally {
            // 3. Restauramos o limpiamos
            if (originalTenant != null) {
                TenantContext.setCurrentTenant(originalTenant);
            } else {
                TenantContext.clear();
            }
        }
    }
}