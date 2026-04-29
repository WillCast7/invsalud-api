package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.OrderDTO;
import com.aurealab.dto.OrderRequestDTO;
import com.aurealab.service.Inventory.OrderService;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sales")
public class SaleController {

    @Autowired
    OrderService orderService;

    @GetMapping(produces = "application/json", value = "/special")
    public ResponseEntity<APIResponseDTO<String>> getSpecialsSalesForTable(@RequestParam(defaultValue = "1") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(defaultValue = "") String searchValue) {
        return orderService.getOrders(page, size, searchValue, true, constants.productTypes.SpecialControl);
    }

    @GetMapping(produces = "application/json", value = "/public")
    public ResponseEntity<APIResponseDTO<String>> getPublicSalesForTable(@RequestParam(defaultValue = "1") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(defaultValue = "") String searchValue) {
        return orderService.getOrders(page, size, searchValue, true, constants.productTypes.PublicHealth);
    }

    @GetMapping(produces = "application/json", value = "/recipe")
    public ResponseEntity<APIResponseDTO<String>> getRecipeSalesForTable(@RequestParam(defaultValue = "1") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(defaultValue = "") String searchValue) {
        return orderService.getOrders(page, size, searchValue, true, constants.productTypes.Recipe);
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<OrderDTO>> savePublicSale(@RequestBody OrderRequestDTO orderRequestDTO) {
        return orderService.savePublicSale(orderRequestDTO);
    }

}
