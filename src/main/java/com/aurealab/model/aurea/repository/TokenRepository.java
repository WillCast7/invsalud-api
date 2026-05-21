package com.aurealab.model.aurea.repository;

import com.aurealab.model.aurea.entity.PasswordResetTokenEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {
    Optional<PasswordResetTokenEntity> findByToken(String token);
}
