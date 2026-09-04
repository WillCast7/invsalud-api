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
@Table(name = "companies")
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nit;

    private String name;

    @Column(name = "legal_name")
    private String legalName;

    @Column(name = "tax_id")
    private String taxId;

    private String email;

    private String phone;

    private String address;

    private String country;

    private String type;

    private String city;

    private String website;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "logo_order")
    private String logoOrder;

    @Column(name = "logo_sold")
    private String logoSold;

    @Column(name = "logo_purchasing")
    private String logoPurchasing;

    @Column(name = "subscription_plan")
    private String subscriptionPlan;

    @Column(name = "name_app")
    private String nameApp;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private Boolean isActive;

    private int iva;

    @Column(name = "use_iva")
    private Boolean useIva;

    private String footer;

    @Column(name = "days_limit_resolution")
    private int daysLimitResolution;

}
