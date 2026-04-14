package com.aurealab.model.aurea.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
    @Table(name = "payments")
    public class PaymentEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "company_id", nullable = false) // Clave foránea
        private CompanyEntity company;

        private BigDecimal amount;

        @Column(name = "payment_date")
        private LocalDateTime paymentDate;

        @Column(name = "payment_status")
        private String paymentStatus;

        @Column(name = "transaction_id")
        private String transactionId;
    }
