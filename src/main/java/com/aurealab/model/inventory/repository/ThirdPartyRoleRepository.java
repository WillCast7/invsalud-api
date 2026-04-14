package com.aurealab.model.inventory.repository;

import com.aurealab.model.inventory.entity.TPRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ThirdPartyRoleRepository extends JpaRepository<TPRoleEntity, Long> {
    public Set<TPRoleEntity> findByRoleName(String roleName);
}
