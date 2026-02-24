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
        return ResponseEntity.ok(APIResponseDTO.success(findPendingChargeByThirdParty(thirdPartyId), constants.success.findedSuccess));
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

    public ChargeDTO saveIncome(ThirdPartyDTO thirdParty, BigDecimal expectedAmount, BigDecimal receivedAmount) {

        // 1. Buscamos si ya existe una cuenta pendiente para este tercero Y este concepto/venta específica
        // (Idealmente deberías buscar por un ID de Venta o Servicio, no solo por ThirdPartyId)
        ChargeDTO chargeExisting = findPendingChargeByThirdParty(thirdParty.id());

        ChargeDTO chargeToSave;

        if (chargeExisting != null) {
            // --- ESCENARIO A: EL CLIENTE ESTÁ ABONANDO A UNA DEUDA EXISTENTE ---
            BigDecimal newPaidAmount = chargeExisting.paidAmount().add(receivedAmount);
            BigDecimal newBalance = chargeExisting.totalAmount().subtract(newPaidAmount);

            chargeToSave = ChargeDTO.builder()
                    .id(chargeExisting.id()) // Mantener el ID para actualizar
                    .thirdParty(thirdParty)
                    .totalAmount(chargeExisting.totalAmount()) // El total NO CAMBIA
                    .paidAmount(newPaidAmount)                 // El pagado SUBE
                    .balance(newBalance)                       // El saldo BAJA
                    .status(newBalance.compareTo(BigDecimal.ZERO) <= 0
                            ? constants.configParam.statusTransactionPaid
                            : constants.configParam.statusTransactionPartial)
                    .createdBySystemUserId(jwtUtils.getCurrentUserId())
                    .build();
        } else {
            // --- ESCENARIO B: ES UNA COMPRA NUEVA ---
            BigDecimal balance = expectedAmount.subtract(receivedAmount);

            chargeToSave = ChargeDTO.builder()
                    .thirdParty(thirdParty)
                    .totalAmount(expectedAmount) // Valor total del servicio
                    .paidAmount(receivedAmount)   // Lo que entregó hoy
                    .balance(balance)            // Lo que queda debiendo
                    .status(balance.compareTo(BigDecimal.ZERO) <= 0
                            ? constants.configParam.statusTransactionPaid
                            : constants.configParam.statusTransactionPartial)
                    .createdBySystemUserId(jwtUtils.getCurrentUserId())
                    .build();
        }
        System.out.println("chargeToSave");
        System.out.println(chargeToSave);
        System.out.println(chargeToSave.balance());
        System.out.println(chargeToSave.paidAmount());
        System.out.println(chargeToSave.totalAmount());

        return tenantService.executeInTenant(tenancy, () -> {
            ChargeEntity entity = ChargeMapper.toEntity(chargeToSave);
            return ChargeMapper.toDto(chargeRepository.save(entity));
        });
    }

    public ChargeDTO saveExpense(ThirdPartyDTO thirdParty, BigDecimal amount) {
        // 1. Buscamos si tenemos una cuenta por pagar pendiente con este tercero
        ChargeDTO chargeExisting = findPendingChargeByThirdParty(thirdParty.id());

        ChargeDTO chargeToSave;

        if (chargeExisting != null) {
            // --- ESCENARIO A: ESTAMOS PAGANDO UNA DEUDA EXISTENTE (Egreso que reduce saldo) ---
            // Si el total_amount era negativo (deuda nuestra), al sumar el pago se acerca a cero
            BigDecimal newPaidAmount = chargeExisting.paidAmount().add(amount);
            BigDecimal newBalance = chargeExisting.totalAmount().subtract(newPaidAmount);

            chargeToSave = ChargeDTO.builder()
                    .id(chargeExisting.id())
                    .thirdParty(thirdParty)
                    .totalAmount(chargeExisting.totalAmount())
                    .paidAmount(newPaidAmount)
                    .balance(newBalance)
                    .status(newBalance.compareTo(BigDecimal.ZERO) <= 0
                            ? constants.configParam.statusTransactionPaid
                            : constants.configParam.statusTransactionPartial)
                    .createdBySystemUserId(jwtUtils.getCurrentUserId())
                    .build();
        } else {
            // --- ESCENARIO B: GASTO DIRECTO / NUEVA CUENTA POR PAGAR ---
            // El totalAmount representa el costo del gasto.
            // Como es una salida inmediata, el pagado es igual al total.
            chargeToSave = ChargeDTO.builder()
                    .thirdParty(thirdParty)
                    .totalAmount(amount)
                    .paidAmount(amount)
                    .balance(BigDecimal.ZERO) // Gasto pagado al momento
                    .status(constants.configParam.statusTransactionPaid)
                    .createdBySystemUserId(jwtUtils.getCurrentUserId())
                    .build();
        }

        return tenantService.executeInTenant(tenancy, () -> {
            ChargeEntity entity = ChargeMapper.toEntity(chargeToSave);
            return ChargeMapper.toDto(chargeRepository.save(entity));
        });
    }

    public ChargeDTO findPendingChargeByThirdParty(Long id){
        return tenantService.executeInTenant(tenancy, () -> {
            return ChargeMapper.toDto(chargeRepository.findPendingChargeByThirdParty(id));
        });
    }
}
