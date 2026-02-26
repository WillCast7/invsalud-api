package com.aurealab.service.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.CashSessionDTO;
import com.aurealab.dto.CashRegister.request.CashMovementRequestDTO;
import com.aurealab.dto.CashRegister.response.*;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Set;

public interface CashMovementService {
    public ResponseEntity<APIResponseDTO<CashSessionsResponseDTO>> getAllDayTransactions(int page, int size, String searchValue);
    ResponseEntity<APIResponseDTO<Object>> getIncomeFormParams();
    ResponseEntity<APIResponseDTO<Object>> getExpenseFormParams();
    public CashMovementResponseDTO saveMovement(CashMovementRequestDTO movement, Long chargeId, Long customerId, String type, String expense);
    public ResponseEntity<APIResponseDTO<String>> saveIncomeTransaction(ThirdPartyDTO thirdParty, CashMovementRequestDTO transaction);
    public ResponseEntity<APIResponseDTO<String>> saveExpenseTransaction(ThirdPartyDTO thirdParty, CashMovementRequestDTO transaction);
    public ResponseEntity<APIResponseDTO<CashSessionSummaryDTO>> calculateTotalAmount(Long id);
    public Set<CashMovementResponseDTO> findAllByCashSessionId(Long id);
    public CashSessionSummaryDTO getSummaries(Long id);
    public CashMovementResponseDTO findById(Long id);
    public ResponseEntity<APIResponseDTO<CashMovementResponseDTO>> findCashMovementById(Long id);
    public ResponseEntity<APIResponseDTO<CashSessionDetailsResponseDTO>> getCashSessionDetailsById(int page, int size, Long id);
}
