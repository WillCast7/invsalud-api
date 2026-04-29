package com.aurealab.model.cashRegister.repository;

import com.aurealab.model.cashRegister.entity.FollowingItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowingItemRepository extends JpaRepository<FollowingItemEntity, Long> {
    Optional<FollowingItemEntity> findById(Long id);
}
