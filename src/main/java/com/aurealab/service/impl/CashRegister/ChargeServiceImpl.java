package com.aurealab.service.impl.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ChargeDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.mapper.CashRegister.ChargeMapper;
import com.aurealab.model.cashRegister.entity.ChargeEntity;
import com.aurealab.model.cashRegister.repository.ChargeRepository;
import com.aurealab.service.CashRegister.ChargeService;
import com.aurealab.service.impl.shared.TenantService;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChargeServiceImpl implements ChargeService {
    private String tenancy = "conduvalle";
    @Autowired
    ChargeRepository chargeRepository;

    @Autowired
    private TenantService tenantService;

    @Autowired
    JwtUtils jwtUtils;

    public ChargeDTO findById(Long id){
        Optional<ChargeEntity> chargeFinded = chargeRepository.findById(id);
        return chargeFinded.map(ChargeMapper::toDto).orElse(null);
    }

    public ResponseEntity<APIResponseDTO<ChargeDTO>> findByCustomerId(Long thirdPartyId){
        return ResponseEntity.ok(APIResponseDTO.success(findByThirdPartyId(thirdPartyId), constants.success.findedSuccess));
    }

    public ChargeDTO findByThirdPartyId(Long thirdPartyId){
        System.out.println("thirdPartyId");
        System.out.println(thirdPartyId);
        try {
            ChargeEntity chargeEntity = tenantService.executeInTenant(tenancy, () -> {
                return chargeRepository.findByThirdPartyId(thirdPartyId);
            });

            return ChargeMapper.toDTOWithOutThirdParty(
                    chargeEntity
            );
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<ChargeDTO> findAll(){
         List<ChargeDTO> chargesFinded = new ArrayList<>();
         chargeRepository.findAll().forEach(chargeEntity -> chargesFinded.add(ChargeMapper.toDto(chargeEntity)));
        return chargesFinded;
    }

    public ChargeDTO saveIncome(ThirdPartyDTO thirdParty, BigDecimal totalAmount){

        ChargeDTO chargeThirdParty = findByThirdPartyId(thirdParty.id());
        ChargeDTO charge = chargeThirdParty != null ?
                ChargeDTO.builder()
                    .id(chargeThirdParty.id())
                    .thirdParty(thirdParty)
                    .totalAmount(chargeThirdParty.totalAmount().subtract(totalAmount))
                    .status(chargeThirdParty.totalAmount().subtract(totalAmount)
                    .compareTo(BigDecimal.ZERO) <= 0 ?
                            constants.configParam.statusTransactionPaid : constants.configParam.statusTransactionPartial)
                    .setCreatedBySystemUserId(jwtUtils.getCurrentUserId())
                    .build() :
                ChargeDTO.builder()
                    .thirdParty(thirdParty)
                    .totalAmount(totalAmount)
                    .status(constants.configParam.statusTransactionPartial)
                    .setCreatedBySystemUserId(jwtUtils.getCurrentUserId())
                    .build();

        return tenantService.executeInTenant(tenancy, () -> {
            return ChargeMapper.toDto(chargeRepository.save(ChargeMapper.toEntity(charge)));
        });
    }

    public ChargeDTO saveExpense(ThirdPartyDTO thirdParty, BigDecimal totalAmount){
        ChargeDTO chargeThirdParty = findByThirdPartyId(thirdParty.id());
        System.out.println("chargeThirdParty");
        System.out.println(thirdParty.id());
        System.out.println(chargeThirdParty);
        ChargeDTO charge = chargeThirdParty != null ?
                ChargeDTO.builder()
                        .id(chargeThirdParty.id())
                        .thirdParty(thirdParty)
                        .totalAmount(chargeThirdParty.totalAmount().subtract(totalAmount))
                        .status(constants.configParam.expenseTransaction)
                        .setCreatedBySystemUserId(jwtUtils.getCurrentUserId())
                        .build() :
                ChargeDTO.builder()
                        .thirdParty(thirdParty)
                        .totalAmount(totalAmount.negate())
                        .status(constants.configParam.expenseTransaction)
                        .setCreatedBySystemUserId(jwtUtils.getCurrentUserId())
                        .build();

        return tenantService.executeInTenant(tenancy, () -> {
            return ChargeMapper.toDto(chargeRepository.save(ChargeMapper.toEntity(charge)));
        });
    }
}
