package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.OrderDTO;
import com.aurealab.dto.OrderRequestDTO;
import com.aurealab.dto.PrescriptionInventoryDTO;
import com.aurealab.service.Inventory.OrderService;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping(produces = "application/json", value = "/special")
    public ResponseEntity<APIResponseDTO<String>> getOrdersForTable(@RequestParam(defaultValue = "1") int page,
                                                                                   @RequestParam(defaultValue = "10") int size,
                                                                                   @RequestParam(defaultValue = "") String searchValue) {
        return orderService.getOrders(page, size, searchValue, false, constants.productTypes.SpecialControl);
    }

    @GetMapping(produces = "application/json", value = "/recipe")
    public ResponseEntity<APIResponseDTO<String>> getOrdersRecipeForTable(@RequestParam(defaultValue = "1") int page,
                                                                                   @RequestParam(defaultValue = "10") int size,
                                                                                   @RequestParam(defaultValue = "") String searchValue) {
        return orderService.getOrders(page, size, searchValue, false, constants.productTypes.Recipe);
    }

    @GetMapping(produces = "application/json", value = "/sales")
    public ResponseEntity<APIResponseDTO<String>> getSalesForTable(@RequestParam(defaultValue = "1") int page,
                                                                                   @RequestParam(defaultValue = "10") int size,
                                                                                   @RequestParam(defaultValue = "") String searchValue) {
        return orderService.getOrders(page, size, searchValue, true, constants.productTypes.SpecialControl);
    }

    @GetMapping(produces = "application/json", value = "/{id}")
    public ResponseEntity<APIResponseDTO<OrderDTO>> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<OrderDTO>> saveOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        return orderService.saveOrder(orderRequestDTO);
    }

    @PutMapping(produces = "application/json", value = "/abort/{id}")
    public ResponseEntity<APIResponseDTO<OrderDTO>> abortOrder(@PathVariable Long id) {
        return orderService.abortOrder(id);
    }

    @PostMapping(produces = "application/json", value = "/sell/{id}")
    public ResponseEntity<APIResponseDTO<OrderDTO>> sellOrder(@PathVariable Long id) {
        return orderService.sellOrder(id);
    }
}
