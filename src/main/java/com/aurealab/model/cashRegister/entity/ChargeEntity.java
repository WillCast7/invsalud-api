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
    @JoinColumn(name = "person_id", nullable = false)
    private ThirdPartyEntity person;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalAmount;

    private String status = "PENDING";

    private String description;

    @OneToMany(mappedBy = "charge", cascade = CascadeType.ALL)
    private List<ChargeProductEntity> items;
}