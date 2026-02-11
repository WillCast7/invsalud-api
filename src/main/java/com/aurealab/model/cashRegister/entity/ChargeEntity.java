package com.aurealab.model.cashRegister.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "charges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChargeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thirdparty_id", nullable = false)
    private ThirdPartyEntity thirdParty;

    @Column(precision = 19, scale = 4, name = "total_amount")
    private BigDecimal totalAmount;

    private String status = "PENDING";

    private String description;

    @Column(name = "created_by_system_user_id", nullable = false)
    private Long createdBySystemUserId;

    public ChargeEntity(Long id){
        this.id = id;
    }
}