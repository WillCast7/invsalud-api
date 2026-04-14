package com.aurealab.dto.response;

import com.aurealab.dto.CompanyDTO;
import com.aurealab.dto.PersonDTO;
import com.aurealab.dto.RoleDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserTableResponseDTO {

    private Long id;
    private String email;
    private String userName;
    private String documentType;
    private String documentNumber;
    private String fullName;
    private String phoneNumber;
    private String address;
    private LocalDate birthDate;


}
