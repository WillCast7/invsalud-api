package com.aurealab.dto;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequestDTO(@NotBlank String username,
                                       @NotBlank String dniNumber) {
}
