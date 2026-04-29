package com.aurealab.service.Inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.OrderDTO;
import com.aurealab.dto.OrderRequestDTO;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    public ResponseEntity<APIResponseDTO<String>> getOrders(int page, int size, String searchValue, boolean isSold, String type);
    public ResponseEntity<APIResponseDTO<OrderDTO>> getOrderById(Long id);
    public ResponseEntity<APIResponseDTO<OrderDTO>> saveOrder(OrderRequestDTO orderRequestDTO);
    public ResponseEntity<APIResponseDTO<OrderDTO>> abortOrder(Long id);
    public ResponseEntity<APIResponseDTO<OrderDTO>> sellOrder(Long id);
    public ResponseEntity<APIResponseDTO<OrderDTO>> savePublicSale(OrderRequestDTO request);
}
