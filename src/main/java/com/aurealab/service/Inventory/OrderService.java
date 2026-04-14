package com.aurealab.service.Inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.OrderDTO;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    public ResponseEntity<APIResponseDTO<String>> getOrders(int page, int size, String searchValue);
    public ResponseEntity<APIResponseDTO<OrderDTO>> getOrderById(Long id);
}
