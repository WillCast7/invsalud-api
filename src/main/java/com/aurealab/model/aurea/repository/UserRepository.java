package com.aurealab.model.aurea.repository;

import com.aurealab.model.aurea.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long>, ListPagingAndSortingRepository<UserEntity, Long> {

    Optional<UserEntity> findByUserNameOrEmail(String userName, String email);

    @EntityGraph(attributePaths = {"person", "role", "company"})
    Page<UserEntity> findAll(Specification<UserEntity> spec, Pageable pageable);
}
