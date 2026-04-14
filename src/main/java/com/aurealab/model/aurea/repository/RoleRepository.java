package com.aurealab.model.aurea.repository;

import com.aurealab.model.aurea.entity.RoleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

    @Query("SELECT re FROM RoleEntity re WHERE re.role = :role")
    Optional<RoleEntity> findByName(String role);


    Optional<RoleEntity> findByRole(@Param("role") String role);

    @Override
    Set<RoleEntity> findAll();
}
