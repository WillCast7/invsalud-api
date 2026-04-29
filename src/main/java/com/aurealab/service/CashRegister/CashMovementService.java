package com.aurealab.service.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.CashMovementItemDTO;
import com.aurealab.dto.CashRegister.CashSessionDTO;
import com.aurealab.dto.CashRegister.FollowingDTO;
import com.aurealab.dto.CashRegister.request.CashMovementRequestDTO;
import com.aurealab.dto.CashRegister.response.*;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Set;
import java.util.List;

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
    public Set<CashMovementResponseDTO> findAllByCustomerId(Long id);
    public List<CashMovementResponseDTO> findAllByCustomerIdOrderByCreatedAtDesc(Long id);
    public CashMovementResponseDTO findActiveFollowingByCustomerId(Long id);
    public Set<CashMovementResponseDTO> findItemsByCustomerId(Long id);
    public ResponseEntity<APIResponseDTO<CashMovementResponseDTO>> findCashMovementById(Long id);
    public ResponseEntity<APIResponseDTO<CashSessionDetailsResponseDTO>> getCashSessionDetailsById(int page, int size, Long id);
    ResponseEntity<APIResponseDTO<String>> updateFollowingItemStatus(Long id, String status);
    ResponseEntity<APIResponseDTO<String>> updateCashMovementItemStatus(Long id, String status);
    ResponseEntity<APIResponseDTO<String>> finishItemsCase(Long movementId);
    ResponseEntity<APIResponseDTO<String>> finishFollowingCase(Long movementId);
}
