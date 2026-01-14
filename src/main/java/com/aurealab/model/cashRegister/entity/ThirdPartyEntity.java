package com.aurealab.model.cashRegister.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "thirdparties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThirdPartyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dni_type", nullable = false, length = 10)
    private String dniType;

    @Column(name = "dni_number", nullable = false, unique = true, length = 20)
    private String dniNumber;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String email;
    private String address;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "created_by_system_user_id", nullable = false)
    private Long createdBySystemUserId;

    @ManyToMany
    @JoinTable(
            name = "person_roles",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<ThirdPartyRoleEntity> roleEntities = new HashSet<>();
}
