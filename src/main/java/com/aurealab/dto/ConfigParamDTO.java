package com.aurealab.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ConfigParamDTO {
    private Long id;
    private String name;
    private String shortname;
    private String parent;
    private String definition;
    private int order;
    private boolean isActive;
}
