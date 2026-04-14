package com.aurealab.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Set;

@JsonPropertyOrder({"username", "message", "jwt", "status"})
public record AuthResponse(String username,
                      String names,
                      String jwt,
                      Set<MenuDTO> menus) {

}
