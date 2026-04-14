package com.aurealab.service;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.MenuDTO;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface DashboardService {
    public ResponseEntity<APIResponseDTO<Set<MenuDTO>>> getUsersMenu();
}
