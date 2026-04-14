package com.aurealab.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class DepartmentDTO {
    private String name;
    private String code;
}
