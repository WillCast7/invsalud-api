package com.aurealab.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CustomerTableDTO {
    private String name;
    private String nit;
    private Integer actual;
    private Integer anterior1;
    private Integer anterior2;
    private String city;
    private String asesor;
}
