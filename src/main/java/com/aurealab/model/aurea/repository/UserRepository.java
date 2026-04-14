package com.aurealab.model.aurea.repository;

import com.aurealab.model.aurea.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long>, ListPagingAndSortingRepository<UserEntity, Long> {

    Optional<UserEntity> findByUserNameOrEmail(String userName, String email);
}
