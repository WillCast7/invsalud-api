package com.aurealab.model.specs;

import com.aurealab.model.notification.entity.NotificationEntity;
import com.aurealab.model.notification.entity.UserNotificationEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserNotificationSpecs {

    public static Specification<UserNotificationEntity> filter(Long userId, String category, Boolean isRead, String searchValue) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Siempre restringir al usuario autenticado
            predicates.add(cb.equal(root.get("userId"), userId));

            // Join con notification para traer datos y filtrar
            Join<UserNotificationEntity, NotificationEntity> notificationJoin = root.join("notification");

            // 2. Filtro por categoría (si viene informada)
            if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL") && !category.equalsIgnoreCase("TODAS")) {
                predicates.add(cb.equal(cb.upper(notificationJoin.get("category")), category.trim().toUpperCase()));
            }

            // 3. Filtro por leído / no leído (si viene informado)
            if (isRead != null) {
                predicates.add(cb.equal(root.get("isRead"), isRead));
            }

            // 4. Búsqueda por texto libre en título o mensaje
            if (searchValue != null && !searchValue.trim().isEmpty()) {
                String pattern = "%" + searchValue.toLowerCase().trim() + "%";
                Predicate titlePredicate = cb.like(cb.lower(notificationJoin.get("title")), pattern);
                Predicate messagePredicate = cb.like(cb.lower(notificationJoin.get("message")), pattern);
                Predicate categoryPredicate = cb.like(cb.lower(notificationJoin.get("category")), pattern);
                Predicate priorityPredicate = cb.like(cb.lower(notificationJoin.get("priority")), pattern);
                predicates.add(cb.or(titlePredicate, messagePredicate, categoryPredicate, priorityPredicate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}