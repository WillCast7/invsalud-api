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
    @com.fasterxml.jackson.annotation.JsonProperty("isActive")
    private boolean isActive;

    @com.fasterxml.jackson.annotation.JsonProperty("active")
    public boolean getActive() {
        return this.isActive;
    }

    @com.fasterxml.jackson.annotation.JsonSetter("active")
    public void setActive(boolean active) {
        this.isActive = active;
    }
    private int order;
}
