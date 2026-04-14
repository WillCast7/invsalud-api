package com.aurealab.model.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thirdparty_id")
    private ThirdPartyEntity thirdParty;

    private BigDecimal total;

    private String status; // 'PENDING', 'SOLD'

    private String observations;

    @Column(name = "order_code", unique = true)
    private String orderCode;

    @Column(name = "sold_code")
    private String soldCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "sold_at")
    private LocalDateTime soldAt = LocalDateTime.now();

    @Column(name = "expirate_at")
    private LocalDateTime expirateAt = LocalDateTime.now();

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "sold_by")
    private LocalDateTime soldBy = LocalDateTime.now();

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "is_sold")
    private boolean isSold;

    private String type;  // 'COTIZACION', 'VENTA'

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItemEntity> items;
}
