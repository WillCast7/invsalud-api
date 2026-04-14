package com.aurealab.model.cashRegister.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cases")
public class CaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Se asume relación con CustomerEntity por el icono de llave foránea
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private ThirdPartyEntity customer;

    @Column(name = "created_at")
    private LocalDateTime createdAt; // TIMESTAMPTZ

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}