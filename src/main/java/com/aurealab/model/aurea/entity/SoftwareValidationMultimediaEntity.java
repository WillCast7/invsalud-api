package com.aurealab.model.aurea.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
@Table(name = "softwarevalidation_multimedia", schema = "public")
public class SoftwareValidationMultimediaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "multimedia_order")
    private String multimediaOrder;

    private String description;

    private String multimedia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "softwarevalidationdata_id", referencedColumnName = "id")
    private SoftwareValidationDataEntity softwareValidationData;
}