package com.aurealab.service;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.DashboardResponseDTO;
import com.aurealab.dto.MenuDTO;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface DashboardService {
    public ResponseEntity<APIResponseDTO<DashboardResponseDTO>> getUsersMenu(String startDate, String endDate, Long thirdPartyId, Long productId);
    public ResponseEntity<APIResponseDTO<Object[]>> getMostSell(String thirdParty, String medicine);
}
