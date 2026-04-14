package com.aurealab.model.aurea.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
@Table(name = "5365")
public class SoftwareValidationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    private String description;

    private String father;

    @Column(nullable = false)
    private boolean status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String type;// title, subtitle, comentary, item

    @Column(name = "order")
    private Integer orderValue;

    //@OneToMany(mappedBy = "softwareValidation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    //private List<SoftwareValidationDataEntity> data = new ArrayList<>();

}