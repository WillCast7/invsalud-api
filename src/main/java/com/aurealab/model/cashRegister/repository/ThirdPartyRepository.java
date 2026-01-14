package com.aurealab.model.cashRegister.repository;

import com.aurealab.model.cashRegister.entity.ThirdPartyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThirdPartyRepository extends JpaRepository<ThirdPartyEntity, Long> {
}
