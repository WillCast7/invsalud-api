package com.aurealab.service.impl.CashRegister;

import com.aurealab.dto.CashRegister.CashMovementItemDTO;
import com.aurealab.mapper.CashRegister.CashMovementItemMapper;
import com.aurealab.model.cashRegister.repository.CashMovementProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CashMovementProductServiceImpl {

    @Autowired
    CashMovementProductsRepository chargeProductRepository;


    public CashMovementItemDTO save(CashMovementItemDTO chargeProduct){
        return CashMovementItemMapper.toDto(
                chargeProductRepository.save(
                        CashMovementItemMapper.toEntity(chargeProduct)
                )
        );
    }

    public List<CashMovementItemDTO> saveList(List<CashMovementItemDTO> chargeProduct){
        List<CashMovementItemDTO> list = new ArrayList<>();
        chargeProduct.forEach(chargeProductDTO -> list.add(
                CashMovementItemMapper.toDto(
                        chargeProductRepository.save(
                                CashMovementItemMapper.toEntity(chargeProductDTO)
                        )
                )
        ));
        return list;
    }
}
