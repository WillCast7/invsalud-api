package com.aurealab.model.management.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Getter
@Setter
@Table(name = "configparams")
public class ConfigParamsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String shortname;
    private String parent;
    private String definition;
    private int order;
    @Column(name = "is_active")
    private boolean isActive;

}