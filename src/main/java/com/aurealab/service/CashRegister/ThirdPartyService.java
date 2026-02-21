package com.aurealab.service.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.dto.CashRegister.request.ThirdPartyRequestDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface ThirdPartyService {
    public ResponseEntity<APIResponseDTO<Set<ThirdPartyDTO>>> findCustomersByDocumentNumber(String documentNumber);
    public ThirdPartyDTO saveThirdParty(ThirdPartyDTO thirdParty);
    public List<ThirdPartyDTO> findThirdPartyByRole(String role);
    public ThirdPartyDTO findByDniNumberAndDniType(String documentNumber, String docuentType);
    public ThirdPartyDTO findThirdPartyById(Long id);
    public ResponseEntity<APIResponseDTO<ThirdPartyDTO>> saveCustomer(ThirdPartyRequestDTO thirdPartyDTO);
}
