package com.aurealab.model.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "purchasings")
public class PurchasingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "thirdparty_id", nullable = false)
    private ThirdPartyEntity thirdParty; // El proveedor

    private BigDecimal total;
    private String type;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @Column(name = "purchased_by", updatable = false)
    private String purchasedBy;

    @Column(name = "purchased_code", updatable = false)
    private String purchasedCode;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "purchasing", cascade = CascadeType.ALL)
    private List<PurchasingItemEntity> items;

    @OneToOne(mappedBy = "purchasing", cascade = CascadeType.ALL)
    private PurchasingRecipeEntity purchasingRecipe;
}
