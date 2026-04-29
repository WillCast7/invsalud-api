package com.aurealab.model.aurea.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
@Table(name = "company_configs")
public class CompanyConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "company_id")
    private CompanyEntity company;

    @Column(name = "follow_case")
    private boolean followCase;

    @Column(name = "follow_product")
    private boolean followProduct;
}
