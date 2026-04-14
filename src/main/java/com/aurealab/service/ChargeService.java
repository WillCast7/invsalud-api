package com.aurealab.service;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ChargeDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

public interface ChargeService {
    public ChargeDTO findById(Long id);
    public List<ChargeDTO> findAll();
    public ChargeDTO saveIncome(ThirdPartyDTO thirdParty, BigDecimal expectedAmount, BigDecimal receivedAmount);
    public ChargeDTO saveExpense(ThirdPartyDTO thirdPartyId, BigDecimal totalAmount);
    public ResponseEntity<APIResponseDTO<ChargeDTO>> findByCustomerId(Long id);
    public ChargeDTO findByThirdPartyId(Long thirdPartyId);
    public ChargeDTO findPendingChargeByThirdParty(Long id);

}
