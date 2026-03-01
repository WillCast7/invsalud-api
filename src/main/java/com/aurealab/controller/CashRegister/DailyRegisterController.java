package com.aurealab.controller.CashRegister;


import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.CashSessionDTO;
import com.aurealab.dto.CashRegister.request.SaveTransactionDTO;
import com.aurealab.dto.CashRegister.response.*;
import com.aurealab.service.CashRegister.CashMovementService;
import com.aurealab.service.CashRegister.CashRegisterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Set;

@RestController
@RequestMapping("/dailyregister")
public class DailyRegisterController {

    @Autowired
    CashRegisterService cashRegisterService;

    @Autowired
    CashMovementService cashMovementService;

    @GetMapping(produces = "application/json", value = "/cashiers")
    ResponseEntity<APIResponseDTO<String>>  getDailyCashiers(@RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int size,
                                                              @RequestParam(defaultValue = "") String searchValue){
        return cashRegisterService.GetAllCashSessions(page, size, searchValue);
    }

    @GetMapping(produces = "application/json", value = "/transactions")
    ResponseEntity<APIResponseDTO<CashSessionsResponseDTO>>  getDailyTransactions(@RequestParam(defaultValue = "1") int page,
                                                                                  @RequestParam(defaultValue = "10") int size,
                                                                                  @RequestParam(defaultValue = "") String searchValue){
        return cashMovementService.getAllDayTransactions(page, size, searchValue);
    }

    @GetMapping(produces = "application/json", value = "/transaction/{id}")
    ResponseEntity<APIResponseDTO<CashSessionDetailsResponseDTO>>  getTransactionsById(@RequestParam(defaultValue = "1") int page,
                                                                                       @RequestParam(defaultValue = "10") int size,
                                                                                       @PathVariable Long id){
        return cashMovementService.getCashSessionDetailsById(page, size, id);
    }


    @GetMapping(produces = "application/json", value = "/income/params")
    ResponseEntity<APIResponseDTO<Object>>  getIncomeFormParams(){
        return cashMovementService.getIncomeFormParams();
    }

    @PostMapping(produces = "application/json", value = "/income")
    ResponseEntity<APIResponseDTO<String>>  saveIncome(@RequestBody @Valid SaveTransactionDTO forms){
        return cashMovementService.saveIncomeTransaction(forms.customer(), forms.transaction());
    }

    @GetMapping(produces = "application/json", value = "/expense/params")
    ResponseEntity<APIResponseDTO<Object>>  getExpenseFormParams(){
        return cashMovementService.getExpenseFormParams();
    }

    @PostMapping(produces = "application/json", value = "/expense")
    ResponseEntity<APIResponseDTO<String>>  saveExpense(@RequestBody @Valid SaveTransactionDTO forms){
        return cashMovementService.saveExpenseTransaction(forms.customer(), forms.transaction());
    }

    @GetMapping(produces = "application/json", value = "/totalAmount/{id}")
    public ResponseEntity<APIResponseDTO<CashSessionSummaryDTO>> calculateTotalAmount(@PathVariable Long id){
        return cashMovementService.calculateTotalAmount(id);
    }
}











