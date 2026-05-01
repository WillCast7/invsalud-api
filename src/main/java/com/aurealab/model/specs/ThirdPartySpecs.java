package com.aurealab.model.specs;

import com.aurealab.model.cashRegister.entity.ProductEntity;
import com.aurealab.model.cashRegister.entity.TPRoleEntity;
import com.aurealab.model.cashRegister.entity.ThirdPartyEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class ThirdPartySpecs {

    public static Specification<ThirdPartyEntity> search(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }

            String pattern = "%" + searchTerm.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("documentNumber")), pattern),
                    cb.like(cb.lower(root.get("fullName")), pattern),
                    cb.like(cb.lower(root.get("phoneNumber")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern)
            );
        };
    }

    public static Specification<ThirdPartyEntity> searchProviders() {
        return (root, query, cb) -> {
            // 1. Hacemos un Join con la colección de roles
            // El nombre "roles" debe coincidir con el atributo en ThirdPartyEntity
            Join<ThirdPartyEntity, TPRoleEntity> rolesJoin = root.join("roles");

            // 2. Filtramos por el ID del rol (en este caso, 4 para proveedores)
            return cb.equal(rolesJoin.get("id"), 4L);
        };
    }
}
