package com.aurealab.model.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tp_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TPRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, name = "role_name")
    private String roleName;

    @Column(nullable = false, name = "is_active")
    private boolean isActive = true;
}