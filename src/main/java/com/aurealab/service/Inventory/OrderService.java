package com.aurealab.service.Inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.OrderDTO;
import com.aurealab.dto.OrderRequestDTO;
import com.aurealab.dto.PrescriptionInventoryTableDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface OrderService {
    public ResponseEntity<APIResponseDTO<String>> getOrders(int page, int size, String searchValue, boolean isSold, String type);
    public ResponseEntity<APIResponseDTO<OrderDTO>> getOrderById(Long id);
    public ResponseEntity<APIResponseDTO<OrderDTO>> saveOrder(OrderRequestDTO orderRequestDTO);
    public ResponseEntity<APIResponseDTO<OrderDTO>> abortOrder(Long id);
    public ResponseEntity<APIResponseDTO<OrderDTO>> sellOrder(Long id);
    public ResponseEntity<APIResponseDTO<OrderDTO>> sellOrderRecipe(Long id, com.aurealab.dto.SellRecipeRequestDTO request);
    public ResponseEntity<APIResponseDTO<OrderDTO>> savePublicSale(OrderRequestDTO request);
    public Page<PrescriptionInventoryTableDTO> getOrdersReport(
            int page, int size, Boolean isSold, String type,
            LocalDateTime start, LocalDateTime end,
            String documentNumber, String product, String batch);
}
