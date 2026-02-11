package com.aurealab.service.CashRegister;

import com.aurealab.dto.CashRegister.CashMovementProductsDTO;

import java.util.List;

public interface CashMovementProductService {
    public CashMovementProductsDTO save(CashMovementProductsDTO chargeProduct);
    public List<CashMovementProductsDTO> saveList(List<CashMovementProductsDTO> chargeProduct);
}
