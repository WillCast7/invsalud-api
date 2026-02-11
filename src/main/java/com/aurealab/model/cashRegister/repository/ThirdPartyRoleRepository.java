package com.aurealab.model.cashRegister.repository;

import com.aurealab.model.cashRegister.entity.TPRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ThirdPartyRoleRepository extends JpaRepository<TPRoleEntity, Long> {
    public Set<TPRoleEntity> findByRoleName(String roleName);
}
