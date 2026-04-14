package com.aurealab.model.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String code;

    private String concentration;

    private String presentation;

    @Column(name = "pharmaceutical_form")
    private String pharmaceuticalForm;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_publichealth")
    private Boolean isPublicHealth;

    private String details;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "created_by")
    private Long createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolution_id") // Nombre de la columna en la tabla 'products'
    private ResolutionEntity resolution;

    public ProductEntity(Long id) {
        this.id = id;
    }
}