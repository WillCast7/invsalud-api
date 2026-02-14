package com.aurealab.service.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.CashSessionDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface CashSessionService {
    public ResponseEntity<APIResponseDTO<Set<CashSessionDTO>>> getAllSessions();
    public Set<CashSessionDTO> findAll();
    public Page<CashSessionDTO> findAll(Pageable pageable);
    public CashSessionDTO findTodaySession();
    public CashSessionDTO findOpenedSession();
    public ResponseEntity<APIResponseDTO<CashSessionDTO>> initializeCashSession(CashSessionDTO cashSessionDTO);
    public ResponseEntity<APIResponseDTO<CashSessionDTO>> finalizeCashSession(CashSessionDTO cashSessionDTO);
    public CashSessionDTO findById(Long id);
}
