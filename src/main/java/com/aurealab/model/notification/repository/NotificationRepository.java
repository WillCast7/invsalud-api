package com.aurealab.model.notification.repository;

import com.aurealab.model.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query("SELECT COUNT(n) > 0 FROM NotificationEntity n WHERE n.category = :category AND n.title = :title AND n.createdAt >= :since")
    boolean existsSimilarNotification(@Param("category") String category, @Param("title") String title, @Param("since") OffsetDateTime since);
}