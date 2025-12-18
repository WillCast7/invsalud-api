package com.aurealab.model.emarket.repository;

import com.aurealab.model.aurea.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DepartmentRepository extends CrudRepository<DepartmentEntity, Long> {
    @Query(value="SELECT ID, Nombre, Codigo from E_market_Departamentos_Colombia " +
            "WHERE Codigo <> '0' ORDER BY LOWER(Nombre)", nativeQuery = true)
    List<DepartmentEntity> findAllOrderByName();
}
