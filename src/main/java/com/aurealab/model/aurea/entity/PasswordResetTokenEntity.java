package com.aurealab.model.aurea.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false, name= "user_id")
    private Long userId; // Guardamos el ID del usuario directamente

    @Column(nullable = false, name="expire_date")
    private LocalDateTime expireDate;

    @Column(nullable = false)
    private boolean used; // Control de un solo uso
}
