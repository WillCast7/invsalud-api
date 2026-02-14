package com.aurealab.model.aurea.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
@Table(name = "persons")
public class PersonEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dni_type")
    private String dniType;

    @Column(name = "dni_number")
    private String dniNumber;

    private String names;

    private String surnames;

    @Column(name = "phone", unique = true)
    private String phoneNumber;

    private String address;

    @Column(name = "birth")
    private LocalDate birthDate;
}
