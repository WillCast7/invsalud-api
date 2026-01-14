package com.aurealab.model.cashRegister.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "cash_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CashSessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "opened_at")
    private OffsetDateTime openedAt = OffsetDateTime.now();

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;

    @Column(name = "opening_amount", precision = 19, scale = 4)
    private BigDecimal openingAmount;

    @Column(name = "closing_amount", precision = 19, scale = 4)
    private BigDecimal closingAmount;

    @Column(name = "opened_by_system_user_id")
    private Long openedBySystemUserId;

    @Column(name = "closed_by_system_user_id")
    private Long closedBySystemUserId;

    @Column(length = 20)
    private String status = "OPEN"; // OPEN, CLOSED
}
