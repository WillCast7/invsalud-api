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
@Table(name = "E_market_Ciudades_Colombia", schema = "public")
public class CityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "Nombre")
    private String name;

    @Column(name = "Codigo")
    private String code;

    @Column(name = "CodigoDepartamento", insertable = false, updatable = false)
    private String departmentCode;

    @ManyToOne
    @JoinColumn(name = "CodigoDepartamento", referencedColumnName = "Codigo")
    private DepartmentEntity department;
}
