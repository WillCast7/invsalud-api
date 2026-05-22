package com.aurealab.model.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resolutions")
public class ResolutionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "thirdparty_id", nullable = false)
    @JsonIgnore
    private ThirdPartyEntity thirdParty;

    @Column(nullable = false)
    private String code;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "created_by")
    private String createdBy;

    @OneToMany(mappedBy = "resolution", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ResolutionAllowedProductEntity> allowedProduct = new HashSet<>();
}
