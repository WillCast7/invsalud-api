package com.aurealab.service.impl.shared;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.function.Supplier;

@Component
public class TransactionalExecutor {

    // Esta anotación aquí garantiza que la transacción inicie
    // DESPUÉS de que hayamos cambiado el ThreadLocal en el servicio
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T execute(Supplier<T> operation) {
        return operation.get();
    }
}
