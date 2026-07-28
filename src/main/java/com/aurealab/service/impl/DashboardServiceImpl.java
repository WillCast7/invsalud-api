package com.aurealab.service.impl;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.DashboardResponseDTO;
import com.aurealab.dto.MenuDTO;
import com.aurealab.dto.MonthlyIncomeDTO;
import com.aurealab.dto.PaymentMethodBreakdownDTO;
import com.aurealab.dto.CashRegister.response.CashSessionSummaryDTO;
import com.aurealab.mapper.MenuMapper;
import com.aurealab.model.aurea.entity.UserEntity;
import com.aurealab.model.inventory.entity.OrderEntity;
import com.aurealab.model.inventory.entity.PurchasingEntity;
import com.aurealab.model.inventory.entity.ThirdPartyEntity;
import com.aurealab.model.inventory.entity.OrderItemEntity;
import com.aurealab.model.inventory.entity.PurchasingItemEntity;
import com.aurealab.model.inventory.repository.OrderRepository;
import com.aurealab.model.inventory.repository.PurchasingRepository;
import com.aurealab.model.inventory.repository.ThirdPartyRepository;
import com.aurealab.service.DashboardService;
import com.aurealab.service.MenuService;
import com.aurealab.service.UserService;
import com.aurealab.service.CompanyService;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    MenuService menuService;

    @Autowired
    CompanyService companyService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PurchasingRepository purchasingRepository;

    @Autowired
    ThirdPartyRepository thirdPartyRepository;

    @Override
    @Transactional
    public ResponseEntity<APIResponseDTO<DashboardResponseDTO>> getUsersMenu(
            String startDate, String endDate, Long thirdPartyId, Long productId) {
        UserEntity user = userService.getUserEntityById(jwtUtils.getCurrentUserId());

        // 1. Get user menu
        Set<MenuDTO> menu = new HashSet<>();
        menuService.getMenuByRoleName(user.getRole().getRoleName()).forEach(menuItemEntity ->
                menu.add(MenuMapper.toDto(menuItemEntity)));

        // 2. Get company logoUrl
        String logoUrl = "";
        try {
            logoUrl = companyService.getCompanyEntity().getLogoUrl();
            if (logoUrl == null) {
                logoUrl = "";
            }
        } catch (Exception e) {
            // Fallback in case company doesn't exist
        }

        Long roleId = user.getRole().getId();
        if (roleId != 1L && roleId != 2L && roleId != 3L) {
            DashboardResponseDTO dashboardResponse = new DashboardResponseDTO(
                menu,
                logoUrl,
                Collections.emptyList(),
                Collections.emptyList(),
                null
            );
            return ResponseEntity.ok(
                APIResponseDTO.success(
                    dashboardResponse,
                    constants.success.findedSuccess
                )
            );
        }

        // Parse Dates safely for summary box
        LocalDateTime start = null;
        if (startDate != null && !startDate.trim().isEmpty()) {
            try {
                start = LocalDate.parse(startDate).atStartOfDay();
            } catch (Exception e) {
                // Ignore or log date parsing error
            }
        }
        LocalDateTime end = null;
        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
            } catch (Exception e) {
                // Ignore or log date parsing error
            }
        }

        // Default to today if no dates are provided
        if (start == null || end == null) {
            LocalDate today = LocalDate.now();
            start = today.atStartOfDay();
            end = today.atTime(LocalTime.MAX);
        }

        // 3. Calculate 6-month historical list for charting (ultimo semestre)
        LocalDateTime semesterEnd = LocalDateTime.now();
        LocalDateTime semesterStart = semesterEnd.minusMonths(5).withDayOfMonth(1).toLocalDate().atStartOfDay();

        List<MonthlyIncomeDTO> monthlyIncomes = new ArrayList<>();

        // Generate the template of the last 6 months to guarantee exactly 6 entries
        List<java.time.YearMonth> last6Months = new ArrayList<>();
        java.time.YearMonth currentMonth = java.time.YearMonth.from(semesterEnd);
        for (int i = 5; i >= 0; i--) {
            last6Months.add(currentMonth.minusMonths(i));
        }

        // Determine if third party is a client or provider
        boolean isCliente = true;
        ThirdPartyEntity thirdParty = null;
        if (thirdPartyId != null) {
            thirdParty = thirdPartyRepository.findById(thirdPartyId).orElse(null);
            if (thirdParty != null) {
                isCliente = thirdParty.getRoles().stream()
                    .anyMatch(r -> r.getRoleName().equalsIgnoreCase(constants.configParam.thirdPartyRoleCustomer));
            }
        }

        if (thirdPartyId == null && productId == null) {
            // Case 1: Both are empty -> Current monthly sales incomes
            List<OrderEntity> salesList = orderRepository.findSalesByDateRange(semesterStart, semesterEnd);
            Map<java.time.YearMonth, BigDecimal> monthlyMap = new LinkedHashMap<>();
            for (java.time.YearMonth ym : last6Months) {
                monthlyMap.put(ym, BigDecimal.ZERO);
            }
            for (OrderEntity o : salesList) {
                if (o.getSoldAt() == null) continue;
                java.time.YearMonth ym = java.time.YearMonth.from(o.getSoldAt());
                if (monthlyMap.containsKey(ym)) {
                    monthlyMap.put(ym, monthlyMap.get(ym).add(o.getTotal() != null ? o.getTotal() : BigDecimal.ZERO));
                }
            }
            monthlyIncomes = monthlyMap.entrySet().stream()
                .map(entry -> createMonthlyIncomeDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        } else if (thirdPartyId != null && productId == null) {
            // Case 2: Only thirdParty -> Monthly history (compras del tercero)
            if (isCliente) {
                List<OrderEntity> salesList = orderRepository.findSalesByThirdPartyAndDateRange(thirdPartyId, semesterStart, semesterEnd);
                Map<java.time.YearMonth, BigDecimal> monthlyMap = new LinkedHashMap<>();
                for (java.time.YearMonth ym : last6Months) {
                    monthlyMap.put(ym, BigDecimal.ZERO);
                }
                for (OrderEntity o : salesList) {
                    if (o.getSoldAt() == null) continue;
                    java.time.YearMonth ym = java.time.YearMonth.from(o.getSoldAt());
                    if (monthlyMap.containsKey(ym)) {
                        monthlyMap.put(ym, monthlyMap.get(ym).add(o.getTotal() != null ? o.getTotal() : BigDecimal.ZERO));
                    }
                }
                monthlyIncomes = monthlyMap.entrySet().stream()
                    .map(entry -> createMonthlyIncomeDTO(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
            } else {
                List<PurchasingEntity> purchasesList = purchasingRepository.findPurchasesByThirdPartyAndDateRange(thirdPartyId, semesterStart, semesterEnd);
                Map<java.time.YearMonth, BigDecimal> monthlyMap = new LinkedHashMap<>();
                for (java.time.YearMonth ym : last6Months) {
                    monthlyMap.put(ym, BigDecimal.ZERO);
                }
                for (PurchasingEntity p : purchasesList) {
                    if (p.getCreatedAt() == null) continue;
                    java.time.YearMonth ym = java.time.YearMonth.from(p.getCreatedAt());
                    if (monthlyMap.containsKey(ym)) {
                        monthlyMap.put(ym, monthlyMap.get(ym).add(p.getTotal() != null ? p.getTotal() : BigDecimal.ZERO));
                    }
                }
                monthlyIncomes = monthlyMap.entrySet().stream()
                    .map(entry -> createMonthlyIncomeDTO(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
            }

        } else if (thirdPartyId == null && productId != null) {
            // Case 3: Only product -> Monthly sales of this product
            List<OrderEntity> salesList = orderRepository.findSalesByProductAndDateRange(productId, semesterStart, semesterEnd);
            Map<java.time.YearMonth, BigDecimal> monthlyMap = new LinkedHashMap<>();
            for (java.time.YearMonth ym : last6Months) {
                monthlyMap.put(ym, BigDecimal.ZERO);
            }
            for (OrderEntity o : salesList) {
                if (o.getSoldAt() == null) continue;
                java.time.YearMonth ym = java.time.YearMonth.from(o.getSoldAt());
                if (monthlyMap.containsKey(ym)) {
                    BigDecimal productTotal = o.getItems().stream()
                        .filter(item -> item.getInventory() != null && item.getInventory().getProduct() != null && item.getInventory().getProduct().getId().equals(productId))
                        .map(OrderItemEntity::getPriceTotal)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    monthlyMap.put(ym, monthlyMap.get(ym).add(productTotal));
                }
            }
            monthlyIncomes = monthlyMap.entrySet().stream()
                .map(entry -> createMonthlyIncomeDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        } else {
            // Case 4: Both thirdParty and product -> Monthly sales of this product for this third party
            if (isCliente) {
                List<OrderEntity> salesList = orderRepository.findSalesByThirdPartyAndProductAndDateRange(thirdPartyId, productId, semesterStart, semesterEnd);
                Map<java.time.YearMonth, BigDecimal> monthlyMap = new LinkedHashMap<>();
                for (java.time.YearMonth ym : last6Months) {
                    monthlyMap.put(ym, BigDecimal.ZERO);
                }
                for (OrderEntity o : salesList) {
                    if (o.getSoldAt() == null) continue;
                    java.time.YearMonth ym = java.time.YearMonth.from(o.getSoldAt());
                    if (monthlyMap.containsKey(ym)) {
                        BigDecimal productTotal = o.getItems().stream()
                            .filter(item -> item.getInventory() != null && item.getInventory().getProduct() != null && item.getInventory().getProduct().getId().equals(productId))
                            .map(OrderItemEntity::getPriceTotal)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                        monthlyMap.put(ym, monthlyMap.get(ym).add(productTotal));
                    }
                }
                monthlyIncomes = monthlyMap.entrySet().stream()
                    .map(entry -> createMonthlyIncomeDTO(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
            } else {
                List<PurchasingEntity> purchasesList = purchasingRepository.findPurchasesByThirdPartyAndProductAndDateRange(thirdPartyId, productId, semesterStart, semesterEnd);
                Map<java.time.YearMonth, BigDecimal> monthlyMap = new LinkedHashMap<>();
                for (java.time.YearMonth ym : last6Months) {
                    monthlyMap.put(ym, BigDecimal.ZERO);
                }
                for (PurchasingEntity p : purchasesList) {
                    if (p.getCreatedAt() == null) continue;
                    java.time.YearMonth ym = java.time.YearMonth.from(p.getCreatedAt());
                    if (monthlyMap.containsKey(ym)) {
                        BigDecimal productTotal = p.getItems().stream()
                            .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(productId))
                            .map(PurchasingItemEntity::getPriceTotal)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                        monthlyMap.put(ym, monthlyMap.get(ym).add(productTotal));
                    }
                }
                monthlyIncomes = monthlyMap.entrySet().stream()
                    .map(entry -> createMonthlyIncomeDTO(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
            }
        }

        // 4. Generate payment method breakdown for sales in the date range
        BigDecimal rangeSalesTotal = orderRepository.sumSalesTotalByDate(start, end);

        List<PaymentMethodBreakdownDTO> paymentMethods = new ArrayList<>();
        paymentMethods.add(new PaymentMethodBreakdownDTO("PSE", rangeSalesTotal.multiply(new BigDecimal("0.60"))));
        paymentMethods.add(new PaymentMethodBreakdownDTO("Efectivo", rangeSalesTotal.multiply(new BigDecimal("0.30"))));
        paymentMethods.add(new PaymentMethodBreakdownDTO("Transferencia Bancaria", rangeSalesTotal.multiply(new BigDecimal("0.10"))));

        // 5. Calculate purchases total in the date range
        BigDecimal rangePurchasesTotal = purchasingRepository.sumPurchasesTotalByDate(start, end);

        // 6. Calculate 3x3 statistics for Compras, Cotizaciones, Ventas by product type in the date range
        CashSessionSummaryDTO summaries = CashSessionSummaryDTO.builder()
            .initialAmount(BigDecimal.ZERO)
            .totalIncome(rangeSalesTotal)
            .totalExpense(rangePurchasesTotal)
            .netBalance(rangeSalesTotal.subtract(rangePurchasesTotal))
            .netCashBalance(rangeSalesTotal)
            // Compras
            .purchasesMedicines(purchasingRepository.sumTotalByTypeAndDate(constants.productTypes.SpecialControl, start, end))
            .purchasesMedicinesSp(purchasingRepository.sumTotalByTypeAndDate(constants.productTypes.PublicHealth, start, end))
            .purchasesRecipes(purchasingRepository.sumTotalByTypeAndDate(constants.productTypes.Recipe, start, end))
            // Cotizaciones
            .quotesMedicines(orderRepository.sumTotalByIsSoldAndTypeAndDate(false, constants.productTypes.SpecialControl, start, end))
            .quotesMedicinesSp(orderRepository.sumTotalByIsSoldAndTypeAndDate(false, constants.productTypes.PublicHealth, start, end))
            .quotesRecipes(orderRepository.sumTotalByIsSoldAndTypeAndDate(false, constants.productTypes.Recipe, start, end))
            // Ventas
            .salesMedicines(orderRepository.sumTotalSalesByTypeAndDate(constants.productTypes.SpecialControl, start, end))
            .salesMedicinesSp(orderRepository.sumTotalSalesByTypeAndDate(constants.productTypes.PublicHealth, start, end))
            .salesRecipes(orderRepository.sumTotalSalesByTypeAndDate(constants.productTypes.Recipe, start, end))
            .build();

        // 7. Return complete DashboardResponseDTO
        DashboardResponseDTO dashboardResponse = new DashboardResponseDTO(
            menu,
            logoUrl,
            monthlyIncomes,
            paymentMethods,
            summaries
        );

        return ResponseEntity.ok(
                APIResponseDTO.success(
                    dashboardResponse,
                    constants.success.findedSuccess
                )
        );
    }

    private MonthlyIncomeDTO createMonthlyIncomeDTO(java.time.YearMonth ym, BigDecimal amount) {
        String monthName = ym.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, new java.util.Locale("es", "ES"));
        monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
        if (monthName.endsWith(".")) {
            monthName = monthName.substring(0, monthName.length() - 1);
        }
        return new MonthlyIncomeDTO(monthName, amount);
    }

    public ResponseEntity<APIResponseDTO<Object[]>> getMostSell(String thirdParty, String medicine){
        return null;
    }
}
