package com.aurealab.service.CashRegister;

import com.aurealab.dto.CashRegister.CashMovementItemDTO;

import java.util.List;

public interface CashMovementProductService {
    public CashMovementItemDTO save(CashMovementItemDTO chargeProduct);
    public List<CashMovementItemDTO> saveList(List<CashMovementItemDTO> chargeProduct);
}
