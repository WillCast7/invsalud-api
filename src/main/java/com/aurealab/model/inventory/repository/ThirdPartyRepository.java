package com.aurealab.model.inventory.repository;

import com.aurealab.model.inventory.entity.ThirdPartyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ThirdPartyRepository extends JpaRepository<ThirdPartyEntity, Long> {

    @Query("SELECT tp " +
            "FROM ThirdPartyEntity tp " +
            "JOIN FETCH tp.roles r " +
            "WHERE tp.documentNumber like CONCAT('%', :documentNumber, '%')")
    Set<ThirdPartyEntity> findByDniNumberContaining(String documentNumber);

    @Query("SELECT tp " +
            "FROM ThirdPartyEntity tp " +
            "JOIN FETCH tp.roles r " +
            "WHERE tp.documentNumber = :documentNumber " +
            "AND tp.documentType = :documentType")
    ThirdPartyEntity findByDniNumberAndDniType(@Param("documentNumber") String documentNumber,
                                               @Param("documentType") String documentType);

    @Query("SELECT DISTINCT tp " +
            "FROM ThirdPartyEntity tp " +
            "JOIN FETCH tp.roles r " +
            "WHERE r.roleName = :roleName")
    Set<ThirdPartyEntity> findAllWithRoleByRole(String roleName);
}
