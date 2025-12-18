package com.aurealab.model.emarket.repository;

import com.aurealab.model.aurea.entity.CityEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CityRepository extends CrudRepository<CityEntity, Long> {
    List<CityEntity> findBydepartmentCodeOrderByName(String departamentCode);
}