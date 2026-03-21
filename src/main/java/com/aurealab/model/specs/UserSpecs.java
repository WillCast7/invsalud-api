package com.aurealab.model.specs;

import com.aurealab.model.aurea.entity.CompanyEntity;
import com.aurealab.model.aurea.entity.PersonEntity;
import com.aurealab.model.aurea.entity.UserEntity;
import com.aurealab.util.JwtUtils;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecs {


    public static Specification<UserEntity> search(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }

            String pattern = "%" + searchTerm.toLowerCase() + "%";

            // 1. Realizamos el Join con la entidad PersonEntity
            // 'person' es el nombre del atributo en UserEntity
            Join<UserEntity, PersonEntity> personJoin = root.join("person");

            // 2. Definimos los predicados
            // Campos de UserEntity
            Predicate usernamePredicate = cb.like(cb.lower(root.get("userName")), pattern);
            Predicate emailPredicate = cb.like(cb.lower(root.get("email")), pattern);

            // Campos de PersonEntity (usando el join)
            Predicate namesPredicate = cb.like(cb.lower(personJoin.get("names")), pattern);
            Predicate surnamesPredicate = cb.like(cb.lower(personJoin.get("surnames")), pattern);
            Predicate documentPredicate = cb.like(cb.lower(personJoin.get("documentNumber")), pattern);

            // 3. Unimos todos con un OR
            return cb.or(
                    usernamePredicate,
                    emailPredicate,
                    namesPredicate,
                    surnamesPredicate,
                    documentPredicate
            );
        };
    }

    public static Specification<UserEntity> searchWithCompany(String searchTerm, String tenantName) {
        return (root, query, cb) -> {
            // 1. Filtro base: La compañía siempre debe coincidir (Tenant)
            // Usamos equal porque el nombre del esquema/tenant debe ser exacto
            Join<UserEntity, CompanyEntity> companyJoin = root.join("company");
            Predicate companyAnd = cb.equal(companyJoin.get("tenantName"), tenantName);

            // 2. Si no hay término de búsqueda, devolvemos solo el filtro de compañía
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return companyAnd;
            }

            String pattern = "%" + searchTerm.toLowerCase() + "%";
            Join<UserEntity, PersonEntity> personJoin = root.join("person");

            // 3. Predicados de búsqueda global (OR)
            Predicate searchOr = cb.or(
                    cb.like(cb.lower(root.get("userName")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern),
                    cb.like(cb.lower(personJoin.get("names")), pattern),
                    cb.like(cb.lower(personJoin.get("surnames")), pattern),
                    cb.like(cb.lower(personJoin.get("documentNumber")), pattern)
            );

            // 4. EL TRUCO: Unimos la búsqueda con la compañía usando un AND
            // Resultado final: WHERE (user or person or email...) AND company = 'tenant'
            return cb.and(companyAnd, searchOr);
        };
    }
}