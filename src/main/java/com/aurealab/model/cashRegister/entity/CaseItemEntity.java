package com.aurealab.model.cashRegister.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "case_items")
public class CaseItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    private CaseEntity casse;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "item_name", length = 155, nullable = false)
    private String itemName;

    @Column(name = "item_price", precision = 19, scale = 4, nullable = false)
    private BigDecimal itemPrice;

    @Column(name = "item_type", length = 10, nullable = false)
    private String itemType;

    @Column(name = "execution_status", length = 20)
    private String executionStatus;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "thirdparty_id")
    private Integer thirdpartyId;
}