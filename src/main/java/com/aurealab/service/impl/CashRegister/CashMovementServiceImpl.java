package com.aurealab.service.impl.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.*;
import com.aurealab.dto.CashRegister.request.CashMovementRequestDTO;
import com.aurealab.dto.CashRegister.response.CashMovementResponseDTO;
import com.aurealab.dto.CashRegister.response.CashSessionDetailsResponseDTO;
import com.aurealab.dto.CashRegister.response.CashSessionSummaryDTO;
import com.aurealab.dto.CashRegister.response.CashSessionsResponseDTO;
import com.aurealab.dto.ConfigParamDTO;
import com.aurealab.dto.tables.CashMovementTableDTO;
import com.aurealab.mapper.CashRegister.CashMovementMapper;
import com.aurealab.mapper.CashRegister.PaymentMethodMapper;
import com.aurealab.model.aurea.interfaz.CashSessionSummaryProjection;
import com.aurealab.model.cashRegister.entity.CashMovementEntity;
import com.aurealab.model.cashRegister.entity.CashSessionEntity;
import com.aurealab.model.cashRegister.entity.PaymentMethodEntity;
import com.aurealab.model.cashRegister.repository.CashMovementRepository;
import com.aurealab.model.cashRegister.repository.PaymentMethodRepository;
import com.aurealab.model.cashRegister.repository.ProductRepository;
import com.aurealab.model.cashRegister.specs.CashMovementSpecs;
import com.aurealab.service.CashRegister.*;
import com.aurealab.service.ConfigParamService;
import com.aurealab.service.impl.shared.TenantService;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.*;

@Service
public class CashMovementServiceImpl implements CashMovementService {


    @Autowired
    CashMovementRepository cashMovementRepository;

    @Autowired
    ConfigParamService configParamService;

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private CashSessionService cashSessionService;

    @Autowired
    private ThirdPartyService thirdPartyService;

    @Autowired
    private ThirdPartyRoleService thirdPartyRoleService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    ChargeService chargeService;

    @Autowired
    ProductService productService;

    @Autowired
    DocumentSequenceServiceImpl documentSequenceService;


    private String tenancy = "conduvalle";
    public ResponseEntity<APIResponseDTO<CashSessionsResponseDTO>> getAllDayTransactions(int page, int size, String searchValue) {

        CashSessionsResponseDTO sessions = new CashSessionsResponseDTO(
                cashSessionService.findTodaySession(),
                cashSessionService.findOpenedSession()
            );


        if (sessions.todaySession() == null){
            return ResponseEntity.ok(APIResponseDTO.success(sessions, constants.messages.noData));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<CashMovementTableDTO> cashMovementPage = findAllTableByCashSessionId(sessions.todaySession().id(), searchValue, pageable);
        
        return ResponseEntity.ok(
                APIResponseDTO.withPageable(sessions, constants.messages.consultGood, cashMovementPage)
        );
    }

    public ResponseEntity<APIResponseDTO<CashSessionDetailsResponseDTO>> getCashSessionDetailsById(Long id){

        CashSessionDetailsResponseDTO response = CashSessionDetailsResponseDTO.builder()
                .cashMovements(findAllByCashSessionId(id))
                .cashSession(cashSessionService.findById(id))
                .cashSessionSummary(getSummaries(id))
                .build();

        return ResponseEntity.ok(APIResponseDTO.success(response, constants.success.findedSuccess));
    }

    public ResponseEntity<APIResponseDTO<Object>> getIncomeFormParams(){
        Map<String, Object> response = new HashMap<>();

        Set<PaymentMethodDTO> paymentMethods = new HashSet<>();
        List<ProductDTO> products = new ArrayList<>();
        Set<ConfigParamDTO> documentType;
        List<ThirdPartyDTO> advisors = new ArrayList<>();

        try {
            List<PaymentMethodEntity> paymentMethodEntities = tenantService.executeInTenant(tenancy, () -> {
                return paymentMethodRepository.findAll();
                });

            products = productService.findProductsByType(constants.configParam.incomeTransaction);

            advisors = thirdPartyService.findThirdPartyByRole(constants.roles.advisor);

            paymentMethodEntities.forEach(method-> paymentMethods.add(PaymentMethodMapper.toDto(method)));
            documentType = configParamService.getConfigParamsByParent(constants.configParam.documentType);


        }catch (Exception e){
            throw new RuntimeException(e);
        }

        response.put("documentType", documentType);
        response.put("paymentMethods", paymentMethods);
        response.put("products", products);
        response.put("advisors", advisors);

        return ResponseEntity.ok(APIResponseDTO.success(response, constants.success.findedSuccess ));
    }

    public ResponseEntity<APIResponseDTO<Object>> getExpenseFormParams(){
        Map<String, Object> response = new HashMap<>();
        List<ProductDTO> products = new ArrayList<>();

        Set<PaymentMethodDTO> paymentMethods = new HashSet<>();
        Set<ConfigParamDTO> documentType;

        try {
            List<PaymentMethodEntity> paymentMethodEntities = tenantService.executeInTenant(tenancy, () -> {
                return paymentMethodRepository.findAll();
            });

            products = productService.findProductsByType(constants.configParam.expenseTransaction);

            paymentMethodEntities.forEach(method-> paymentMethods.add(PaymentMethodMapper.toDto(method)));
            documentType = configParamService.getConfigParamsByParent("documentType");

        }catch (Exception e){
            throw new RuntimeException(e);
        }

        response.put("documentType", documentType);
        response.put("products", products);
        response.put("paymentMethods", paymentMethods);

        return ResponseEntity.ok(APIResponseDTO.success(response, constants.success.findedSuccess ));
    }

    public ResponseEntity<APIResponseDTO<String>> saveIncomeTransaction(
            ThirdPartyDTO thirdParty,
            CashMovementRequestDTO income){

        if(cashSessionService.findTodaySession() == null){
            throw new RuntimeException(constants.messages.cashSessionDontExist);
        }

        ThirdPartyDTO customer;

        try{
            ThirdPartyDTO thirdPartySearched = thirdPartyService.findByDniNumberAndDniType(thirdParty.documentType(), thirdParty.documentNumber());
            if(thirdPartySearched == null){
                customer = ThirdPartyDTO.builder()
                        .documentType(thirdParty.documentType())
                        .documentNumber(thirdParty.documentNumber())
                        .fullName(thirdParty.fullName())
                        .email(thirdParty.email())
                        .address(thirdParty.address())
                        .phoneNumber(thirdParty.phoneNumber())
                        .createdBySystemUserId(jwtUtils.getCurrentUserId())
                        .roles(thirdPartyRoleService.findRoleByRoleName(constants.configParam.thirdPartyRoleCustomer))
                        .build();

            }else{
                customer = ThirdPartyDTO.builder()
                        .id(thirdPartySearched.id())
                        .documentType(thirdParty.documentType())
                        .documentNumber(thirdParty.documentNumber())
                        .fullName(thirdParty.fullName())
                        .email(thirdParty.email())
                        .address(thirdParty.address())
                        .phoneNumber(thirdParty.phoneNumber())
                        .createdBySystemUserId(thirdPartySearched.createdBySystemUserId())
                        .roles(thirdPartySearched.roles())
                        .build();
            }
            ThirdPartyDTO customerSaved = thirdPartyService.saveThirdParty(customer);

            ChargeDTO chargeSaved = chargeService.saveIncome(customerSaved, income.expectedAmount(), income.receivedAmount());

            saveMovement(
                    income,
                    chargeSaved.id(),
                    chargeSaved.thirdParty().id(),
                    constants.configParam.incomeTransaction,
                    constants.configParam.incomePrefix
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(APIResponseDTO.success(constants.messages.responseSaveUserGood, constants.success.savedSuccess));
    }

    public ResponseEntity<APIResponseDTO<String>> saveExpenseTransaction(ThirdPartyDTO thirdParty, CashMovementRequestDTO transaction){

        if(cashSessionService.findTodaySession() == null){
            throw new RuntimeException(constants.messages.cashSessionDontExist);
        }

        ThirdPartyDTO thirdPartySearched = thirdPartyService.findThirdPartyById(thirdParty.id());
        ChargeDTO chargeSaved = chargeService.saveExpense(thirdPartySearched, transaction.receivedAmount());

        saveMovement(
                transaction,
                chargeSaved.id(),
                chargeSaved.thirdParty().id(),
                constants.configParam.expenseTransaction,
                constants.configParam.expensePrefix

        );

        return ResponseEntity.ok(APIResponseDTO.success(constants.messages.responseSaveUserGood, constants.success.savedSuccess));

    }

    public ResponseEntity<APIResponseDTO<CashSessionSummaryDTO>> calculateTotalAmount(Long id){
        try{
            return ResponseEntity.ok(APIResponseDTO.success(getSummaries(id), constants.success.findedSuccess));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<CashMovementResponseDTO> findAllByCashSessionId(Long id){
        Specification<CashMovementEntity> spec = CashMovementSpecs.searchBySessionAndTerm(id, null);
        return tenantService.executeInTenant(tenancy, () -> {
            Set<CashMovementResponseDTO> response = new HashSet<>();
            cashMovementRepository.findAll(spec)
                    .forEach(cashMovementEntity -> response
                            .add(CashMovementMapper.toDtoList(cashMovementEntity)));
            return response;
        });
    }

    public Page<CashMovementResponseDTO> findAllByCashSessionId(Long id, String searchValue, Pageable pageable){

        Specification<CashMovementEntity> spec = CashMovementSpecs.searchBySessionAndTerm(id, searchValue);

        return tenantService.executeInTenant(tenancy, () -> {
            Page<CashMovementEntity> entities = cashMovementRepository.findAll(spec, pageable);
            return entities.map(CashMovementMapper::toDtoList);
        });
    }

    public Page<CashMovementTableDTO> findAllTableByCashSessionId(Long id, String searchValue, Pageable pageable){

        Specification<CashMovementEntity> spec = CashMovementSpecs.searchBySessionAndTerm(id, searchValue);

        return tenantService.executeInTenant(tenancy, () -> {
            Page<CashMovementEntity> entities = cashMovementRepository.findAll(spec, pageable);
            return entities.map(CashMovementMapper::toDtoTable);
        });
    }

    public CashMovementResponseDTO saveMovement(CashMovementRequestDTO movement, Long chargeId, Long customerId, String type, String prefix){

        CashMovementEntity cashMovementEntity = CashMovementMapper.toEntity(movement, chargeId, customerId, type);
        String referenceNumber = documentSequenceService.getNextInvoiceNumber(prefix);

        cashMovementEntity.setReferenceNumber(referenceNumber);

        cashMovementEntity.setCreatedBySystemUserId(jwtUtils.getCurrentUserId());

        CashMovementResponseDTO response = tenantService.executeInTenant(tenancy, () -> {
            return CashMovementMapper.toDto(
                    cashMovementRepository.save(cashMovementEntity)
            );
        });
        return response;
    }

    public CashSessionSummaryDTO getSummaries(Long sessionId){
        // 1. Buscamos el resumen en los movimientos
        CashSessionSummaryProjection summary = tenantService.executeInTenant(tenancy, () -> {
            return cashMovementRepository.getSessionSummary(sessionId);
        });

        // 2. Buscamos la sesión para asegurar el monto inicial
        CashSessionDTO session = cashSessionService.findById(sessionId);
        if(session == null ){
            throw new ResourceNotFoundException("Sesión no encontrada");
        }

        // Aseguramos que el monto inicial nunca sea nulo (si es null, usamos 0)
        BigDecimal initial = session.openingAmount() != null ? session.openingAmount() : BigDecimal.ZERO;

        // 3. Si no hay movimientos, enviamos todo en 0 y los balances con el monto inicial
        if (summary == null || summary.getTotalIncome() == null) {
            return CashSessionSummaryDTO.builder()
                    .initialAmount(initial)
                    .totalIncome(BigDecimal.ZERO)
                    .totalExpense(BigDecimal.ZERO)
                    .netBalance(initial)
                    .netCashBalance(initial)
                    .build();
        }

        System.out.println("summary");
        System.out.println(summary);

        // 4. Blindamos las variables de la proyección para que nunca pasen nulas al .add()
        BigDecimal netBalance = summary.getNetBalance() != null ? summary.getNetBalance() : BigDecimal.ZERO;
        BigDecimal netCashBalance = summary.getNetCashBalance() != null ? summary.getNetCashBalance() : BigDecimal.ZERO;
        BigDecimal totalIncome = summary.getTotalIncome() != null ? summary.getTotalIncome() : BigDecimal.ZERO;
        BigDecimal totalExpense = summary.getTotalExpense() != null ? summary.getTotalExpense() : BigDecimal.ZERO;

        // 5. Retornamos con la suma segura
        return CashSessionSummaryDTO.builder()
                .initialAmount(initial)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(initial.add(netBalance)) // ¡Aquí ya no explotará!
                .netCashBalance(initial.add(netCashBalance))
                .build();
    }

    public ResponseEntity<APIResponseDTO<CashMovementResponseDTO>> findCashMovementById(Long id){
        return ResponseEntity.ok(APIResponseDTO.success(findById(id), constants.success.findedSuccess));
    }

    public CashMovementResponseDTO findById(Long id){
        return tenantService.executeInTenant(tenancy, () -> {
            return CashMovementMapper.toDto(cashMovementRepository.findById(id).get());

        });
    }

}
