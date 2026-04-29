package com.aurealab.model.cashRegister.repository;

import com.aurealab.model.cashRegister.entity.FollowingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowingRepository extends JpaRepository<FollowingEntity, Long> {

}
