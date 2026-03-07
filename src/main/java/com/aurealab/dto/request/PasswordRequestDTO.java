package com.aurealab.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordRequestDTO(
        @NotBlank(message = "La contraseña actual es obligatoria")
        @Size(min = 8, message = "La contraseña actual debe tener al menos 8 caracteres")
        String oldPassword,

        @NotBlank(message = "La nueva contraseña no puede estar vacía")
        @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
        String newPassword,

        @NotBlank(message = "La confirmación de la contraseña es obligatoria")
        @Size(min = 8, message = "La confirmación debe tener al menos 8 caracteres")
        String confirmPassword
) {
}