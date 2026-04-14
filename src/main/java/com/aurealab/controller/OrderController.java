package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.OrderDTO;
import com.aurealab.dto.PrescriptionInventoryDTO;
import com.aurealab.service.Inventory.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<String>> getOrdersForTable(@RequestParam(defaultValue = "1") int page,
                                                                                   @RequestParam(defaultValue = "10") int size,
                                                                                   @RequestParam(defaultValue = "") String searchValue) {
        return orderService.getOrders(page, size, searchValue);
    }

    @GetMapping(produces = "application/json", value = "/sales")
    public ResponseEntity<APIResponseDTO<String>> getSalesForTable(@RequestParam(defaultValue = "1") int page,
                                                                                   @RequestParam(defaultValue = "10") int size,
                                                                                   @RequestParam(defaultValue = "") String searchValue) {
        return orderService.getOrders(page, size, searchValue);
    }

    @GetMapping(produces = "application/json", value = "/{id}")
    public ResponseEntity<APIResponseDTO<OrderDTO>> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }
}
