package com.aurealab.model.cashRegister.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cash_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CashMovementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cash_session_id", nullable = false)
    private CashSessionEntity cashSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_id")
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private ChargeEntity charge; //cuenta  del usuario

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private ThirdPartyEntity customer; //cliente, a quien sale esta factura

    private String type;

    @Column(precision = 19, scale = 4, name = "received_amount")
    private BigDecimal receivedAmount;

    @Column(precision = 19, scale = 4, name = "expected_amount")
    private BigDecimal expectedAmount;

    private String concept;

    @ManyToOne
    @JoinColumn(name = "advisor_id", nullable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private ThirdPartyEntity advisor;

    @OneToMany(mappedBy = "cashMovement", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CashMovementItemsEntity> items = new HashSet<>();

    @OneToMany(mappedBy = "cashMovement", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CashMovementPaymentEntity> payments = new HashSet<>();

    @Column(name = "is_void")
    private boolean isVoid = false;

    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "created_by_system_user_id")
    private Long createdBySystemUserId;

    @Column(name = "reference_number")
    private String referenceNumber;

    private String observations;

    @Column(name = "following_is_active")
    private  boolean followingIsActive;

    @OneToOne(mappedBy = "cashMovement", cascade = CascadeType.ALL)
    private FollowingEntity following;
}
