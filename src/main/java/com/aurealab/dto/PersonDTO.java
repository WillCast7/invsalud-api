package com.aurealab.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PersonDTO {

    private Long id;

    private String documentType;

    @NotBlank(message = "El numero de identificacion no puede estar vacío.")
    private String documentNumber;

    @NotBlank(message = "falta llenar los nombres no puede estar vacío.")
    private String names;

    @NotBlank(message = "falta llenar los nombres no puede estar vacío.")
    private String surnames;

    @Size(max = 10, message = "El numero telefonico debe tener 10 numeros")
    private String phoneNumber;

    private String address;

    private LocalDate birthDate;

}
