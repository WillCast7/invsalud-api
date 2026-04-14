package com.aurealab.model.aurea.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
@Table(name = "softwarevalidation_data") // Correcto
public class SoftwareValidationDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean meets;


    @Column(name = "softwarevalidation_code")
    private String softwareValidationCode;

    @OneToMany(mappedBy = "softwareValidationData", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SoftwareValidationMultimediaEntity> multimedia = new ArrayList<>();

}