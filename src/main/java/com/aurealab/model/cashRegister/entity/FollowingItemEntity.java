package com.aurealab.model.cashRegister.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "following_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowingItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private FollowingEntity following;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_price")
    private BigDecimal itemPrice;

    @Column(name = "item_type")
    private String itemType;

    private String status;

    @Column(name = "status_done_at")
    private LocalDateTime statusDoneAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private ThirdPartyEntity provider;
}
