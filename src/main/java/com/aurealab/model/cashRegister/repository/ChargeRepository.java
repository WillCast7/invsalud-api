package com.aurealab.model.cashRegister.repository;

import com.aurealab.model.cashRegister.entity.ChargeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChargeRepository extends JpaRepository<ChargeEntity, Long> {
    @Query("""
        SELECT c
        FROM ChargeEntity c
        JOIN FETCH c.thirdParty tp
        WHERE tp.id = :thirdPartyId
    """)
    ChargeEntity findByThirdPartyId(@Param("thirdPartyId") Long thirdPartyId);

    @Query("""
        SELECT c
        FROM ChargeEntity c
        JOIN FETCH c.thirdParty tp
        WHERE tp.id = :thirdPartyId
        AND c.status = 'PARTIAL'
    """)
    ChargeEntity findPendingChargeByThirdParty(@Param("thirdPartyId") Long thirdPartyId);
}
