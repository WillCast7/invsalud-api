package com.aurealab.model.cashRegister.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cash_movement_payments")
public class CashMovementPaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cash_movement_id")
    private CashMovementEntity cashMovement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethodEntity paymentMethod;

    @Column(precision = 19, scale = 4)
    private BigDecimal amount; // Cuanto se pagó con este método

    @Column(name = "transaction_reference")
    private String transactionReference; // Voucher o número de aprobación
}
