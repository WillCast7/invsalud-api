package com.aurealab.model.cashRegister.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "followings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thirdparty_id")
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private ThirdPartyEntity thirdParty;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "created_by")
    private Long createdBy;

    private String name;

    @Column(name = "is_active")
    private boolean isActive;

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FollowingItemEntity> items;
    
    @OneToOne
    @JoinColumn(name = "cash_movement_id")
    private CashMovementEntity cashMovement;
}
