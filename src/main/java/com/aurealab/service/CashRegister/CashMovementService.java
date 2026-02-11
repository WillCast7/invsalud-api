package com.aurealab.service.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.CashSessionDTO;
import com.aurealab.dto.CashRegister.request.CashMovementRequestDTO;
import com.aurealab.dto.CashRegister.response.CashMovementResponseDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.dto.CashRegister.response.CashSessionSummaryDTO;
import com.aurealab.dto.CashRegister.response.CashSessionsResponseDTO;
import com.aurealab.dto.CashRegister.response.TransactionListSessionResponseDTO;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Set;

public interface CashMovementService {
    public ResponseEntity<APIResponseDTO<CashSessionsResponseDTO>> getAllDayTransactions(int page, int size, String searchValue);
    ResponseEntity<APIResponseDTO<Object>> getIncomeFormParams();
    ResponseEntity<APIResponseDTO<Object>> getExpenseFormParams();
    public CashMovementResponseDTO saveMovement(CashMovementRequestDTO movement, Long chargeId, Long customerId, String type);
    public ResponseEntity<APIResponseDTO<String>> saveIncomeTransaction(ThirdPartyDTO thirdParty, CashMovementRequestDTO transaction);
    public ResponseEntity<APIResponseDTO<String>> saveExpenseTransaction(ThirdPartyDTO thirdParty, CashMovementRequestDTO transaction);
    public ResponseEntity<APIResponseDTO<CashSessionSummaryDTO>> calculateTotalAmount(Long id);
}
