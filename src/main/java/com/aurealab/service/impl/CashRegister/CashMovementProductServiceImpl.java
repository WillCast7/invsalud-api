package com.aurealab.service.impl.CashRegister;

import com.aurealab.dto.CashRegister.CashMovementProductsDTO;
import com.aurealab.mapper.CashRegister.CashMovementProductMapper;
import com.aurealab.model.cashRegister.repository.CashMovementProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CashMovementProductServiceImpl {

    @Autowired
    CashMovementProductsRepository chargeProductRepository;


    public CashMovementProductsDTO save(CashMovementProductsDTO chargeProduct){
        return CashMovementProductMapper.toDto(
                chargeProductRepository.save(
                        CashMovementProductMapper.toEntity(chargeProduct)
                )
        );
    }

    public List<CashMovementProductsDTO> saveList(List<CashMovementProductsDTO> chargeProduct){
        List<CashMovementProductsDTO> list = new ArrayList<>();
        chargeProduct.forEach(chargeProductDTO -> list.add(
                CashMovementProductMapper.toDto(
                        chargeProductRepository.save(
                                CashMovementProductMapper.toEntity(chargeProductDTO)
                        )
                )
        ));
        return list;
    }
}
