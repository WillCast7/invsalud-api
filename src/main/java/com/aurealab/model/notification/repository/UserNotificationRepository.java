package com.aurealab.model.notification.repository;

import com.aurealab.model.notification.entity.UserNotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotificationEntity, Long>, JpaSpecificationExecutor<UserNotificationEntity> {

    @Query("SELECT un FROM UserNotificationEntity un JOIN FETCH un.notification n WHERE un.userId = :userId ORDER BY un.createdAt DESC")
    Page<UserNotificationEntity> findAllByUserIdWithNotification(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(un) FROM UserNotificationEntity un WHERE un.userId = :userId AND un.isRead = false")
    long countUnreadByUserId(@Param("userId") Long userId);

    @Query("SELECT un FROM UserNotificationEntity un JOIN FETCH un.notification n WHERE un.id = :id AND un.userId = :userId")
    Optional<UserNotificationEntity> findByIdAndUserIdWithNotification(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT un FROM UserNotificationEntity un JOIN FETCH un.notification n WHERE un.userId = :userId ORDER BY un.createdAt DESC")
    List<UserNotificationEntity> findTop10ByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Query("UPDATE UserNotificationEntity un SET un.isRead = true, un.readAt = :readAt WHERE un.id = :id AND un.userId = :userId")
    int markAsReadByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId, @Param("readAt") OffsetDateTime readAt);

    @Modifying
    @Query("UPDATE UserNotificationEntity un SET un.isRead = true, un.readAt = :readAt WHERE un.userId = :userId AND (un.isRead = false OR un.isRead IS NULL)")
    int markAllAsReadByUserId(@Param("userId") Long userId, @Param("readAt") OffsetDateTime readAt);
}