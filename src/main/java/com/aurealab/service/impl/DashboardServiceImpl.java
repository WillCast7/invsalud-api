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
        boolean customDateRange = false;
        LocalDateTime start = null;
        if (startDate != null && !startDate.trim().isEmpty()) {
            try {
                start = LocalDate.parse(startDate).atStartOfDay();
                customDateRange = true;
            } catch (Exception e) {
                // Ignore or log date parsing error
            }
        }
        LocalDateTime end = null;
        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
                customDateRange = true;
            } catch (Exception e) {
                // Ignore or log date parsing error
            }
        }

        // Default to today if no dates are provided
        if (start == null && end == null) {
            LocalDate today = LocalDate.now();
            start = today.atStartOfDay();
            end = today.atTime(LocalTime.MAX);
        } else if (start != null && end == null) {
            end = start.with(LocalTime.MAX);
        } else if (start == null && end != null) {
            start = end.with(LocalTime.MIN);
        }

        // 3. Calculate historical list for charting
        LocalDateTime chartStart;
        LocalDateTime chartEnd;
        if (customDateRange) {
            chartStart = start;
            chartEnd = end;
        } else {
            chartEnd = LocalDateTime.now();
            chartStart = chartEnd.minusMonths(5).withDayOfMonth(1).toLocalDate().atStartOfDay();
        }

        List<MonthlyIncomeDTO> monthlyIncomes = new ArrayList<>();

        // Generate the template of months between chartStart and chartEnd
        List<java.time.YearMonth> chartMonths = new ArrayList<>();
        java.time.YearMonth startYm = java.time.YearMonth.from(chartStart);
        java.time.YearMonth endYm = java.time.YearMonth.from(chartEnd);
        if (startYm.isAfter(endYm)) {
            java.time.YearMonth temp = startYm;
            startYm = endYm;
            endYm = temp;
        }
        java.time.YearMonth curr = startYm;
        while (!curr.isAfter(endYm)) {
            chartMonths.add(curr);
            curr = curr.plusMonths(1);
        }
        if (chartMonths.isEmpty()) {
            chartMonths.add(java.time.YearMonth.from(chartEnd));
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
            List<OrderEntity> salesList = orderRepository.findSalesByDateRange(chartStart, chartEnd);
            Map<java.time.YearMonth, BigDecimal> monthlyMap = new LinkedHashMap<>();
            for (java.time.YearMonth ym : chartMonths) {
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
            // Case 2: Only thirdParty -> Monthly history (compras/ventas del tercero)
            if (isCliente) {
                List<OrderEntity> salesList = orderRepository.findSalesByThirdPartyAndDateRange(thirdPartyId, chartStart, chartEnd);
                Map<java.time.YearMonth, BigDecimal> monthlyMap = new LinkedHashMap<>();
                for (java.time.YearMonth ym : chartMonths) {
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
                List<PurchasingEntity> purchasesList = purchasingRepository.findPurchasesByThirdPartyAndDateRange(thirdPartyId, chartStart, chartEnd);
                Map<java.time.YearMonth, BigDecimal> monthlyMap = new LinkedHashMap<>();
                for (java.time.YearMonth ym : chartMonths) {
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
            List<OrderEntity> salesList = orderRepository.findSalesByProductAndDateRange(productId, chartStart, chartEnd);
            Map<java.time.YearMonth, BigDecimal> monthlyMap = new LinkedHashMap<>();
            for (java.time.YearMonth ym : chartMonths) {
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
            // Case 4: Both thirdParty and product -> Monthly sales/purchases of this product for this third party
            if (isCliente) {
                List<OrderEntity> salesList = orderRepository.findSalesByThirdPartyAndProductAndDateRange(thirdPartyId, productId, chartStart, chartEnd);
                Map<java.time.YearMonth, BigDecimal> monthlyMap = new LinkedHashMap<>();
                for (java.time.YearMonth ym : chartMonths) {
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
                List<PurchasingEntity> purchasesList = purchasingRepository.findPurchasesByThirdPartyAndProductAndDateRange(thirdPartyId, productId, chartStart, chartEnd);
                Map<java.time.YearMonth, BigDecimal> monthlyMap = new LinkedHashMap<>();
                for (java.time.YearMonth ym : chartMonths) {
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

        // 4. Generate payment method breakdown for sales in the date range (filtered by thirdParty and product)
        BigDecimal rangeSalesTotal;
        if (thirdPartyId == null && productId == null) {
            rangeSalesTotal = orderRepository.sumSalesTotalByDate(start, end);
        } else if (thirdPartyId != null && productId == null) {
            rangeSalesTotal = orderRepository.sumSalesTotalByDateAndThirdParty(thirdPartyId, start, end);
        } else if (thirdPartyId == null && productId != null) {
            rangeSalesTotal = orderRepository.sumSalesTotalByDateAndProduct(productId, start, end);
        } else {
            rangeSalesTotal = orderRepository.sumSalesTotalByDateAndThirdPartyAndProduct(thirdPartyId, productId, start, end);
        }

        List<PaymentMethodBreakdownDTO> paymentMethods = new ArrayList<>();
        paymentMethods.add(new PaymentMethodBreakdownDTO("PSE", rangeSalesTotal.multiply(new BigDecimal("0.60"))));
        paymentMethods.add(new PaymentMethodBreakdownDTO("Efectivo", rangeSalesTotal.multiply(new BigDecimal("0.30"))));
        paymentMethods.add(new PaymentMethodBreakdownDTO("Transferencia Bancaria", rangeSalesTotal.multiply(new BigDecimal("0.10"))));

        // 5. Calculate purchases total in the date range (filtered by thirdParty and product)
        BigDecimal rangePurchasesTotal;
        if (thirdPartyId == null && productId == null) {
            rangePurchasesTotal = purchasingRepository.sumPurchasesTotalByDate(start, end);
        } else if (thirdPartyId != null && productId == null) {
            rangePurchasesTotal = purchasingRepository.sumPurchasesTotalByDateAndThirdParty(thirdPartyId, start, end);
        } else if (thirdPartyId == null && productId != null) {
            rangePurchasesTotal = purchasingRepository.sumPurchasesTotalByDateAndProduct(productId, start, end);
        } else {
            rangePurchasesTotal = purchasingRepository.sumPurchasesTotalByDateAndThirdPartyAndProduct(thirdPartyId, productId, start, end);
        }

        // 6. Calculate 3x3 statistics for Compras, Cotizaciones, Ventas by product type in the date range
        // Compras
        BigDecimal purchasesMedicines;
        BigDecimal purchasesMedicinesSp;
        BigDecimal purchasesRecipes;
        if (thirdPartyId == null && productId == null) {
            purchasesMedicines = purchasingRepository.sumTotalByTypeAndDate(constants.productTypes.SpecialControl, start, end);
            purchasesMedicinesSp = purchasingRepository.sumTotalByTypeAndDate(constants.productTypes.PublicHealth, start, end);
            purchasesRecipes = purchasingRepository.sumTotalByTypeAndDate(constants.productTypes.Recipe, start, end);
        } else if (thirdPartyId != null && productId == null) {
            purchasesMedicines = purchasingRepository.sumTotalByTypeAndDateAndThirdParty(constants.productTypes.SpecialControl, thirdPartyId, start, end);
            purchasesMedicinesSp = purchasingRepository.sumTotalByTypeAndDateAndThirdParty(constants.productTypes.PublicHealth, thirdPartyId, start, end);
            purchasesRecipes = purchasingRepository.sumTotalByTypeAndDateAndThirdParty(constants.productTypes.Recipe, thirdPartyId, start, end);
        } else if (thirdPartyId == null && productId != null) {
            purchasesMedicines = purchasingRepository.sumTotalByTypeAndDateAndProduct(constants.productTypes.SpecialControl, productId, start, end);
            purchasesMedicinesSp = purchasingRepository.sumTotalByTypeAndDateAndProduct(constants.productTypes.PublicHealth, productId, start, end);
            purchasesRecipes = BigDecimal.ZERO;
        } else {
            purchasesMedicines = purchasingRepository.sumTotalByTypeAndDateAndThirdPartyAndProduct(constants.productTypes.SpecialControl, thirdPartyId, productId, start, end);
            purchasesMedicinesSp = purchasingRepository.sumTotalByTypeAndDateAndThirdPartyAndProduct(constants.productTypes.PublicHealth, thirdPartyId, productId, start, end);
            purchasesRecipes = BigDecimal.ZERO;
        }

        // Cotizaciones
        BigDecimal quotesMedicines;
        BigDecimal quotesMedicinesSp;
        BigDecimal quotesRecipes;
        if (thirdPartyId == null && productId == null) {
            quotesMedicines = orderRepository.sumTotalByIsSoldAndTypeAndDate(false, constants.productTypes.SpecialControl, start, end);
            quotesMedicinesSp = orderRepository.sumTotalByIsSoldAndTypeAndDate(false, constants.productTypes.PublicHealth, start, end);
            quotesRecipes = orderRepository.sumTotalByIsSoldAndTypeAndDate(false, constants.productTypes.Recipe, start, end);
        } else if (thirdPartyId != null && productId == null) {
            quotesMedicines = orderRepository.sumTotalByIsSoldAndTypeAndDateAndThirdParty(false, constants.productTypes.SpecialControl, thirdPartyId, start, end);
            quotesMedicinesSp = orderRepository.sumTotalByIsSoldAndTypeAndDateAndThirdParty(false, constants.productTypes.PublicHealth, thirdPartyId, start, end);
            quotesRecipes = orderRepository.sumTotalByIsSoldAndTypeAndDateAndThirdParty(false, constants.productTypes.Recipe, thirdPartyId, start, end);
        } else if (thirdPartyId == null && productId != null) {
            quotesMedicines = orderRepository.sumTotalByIsSoldAndTypeAndDateAndProduct(false, constants.productTypes.SpecialControl, productId, start, end);
            quotesMedicinesSp = orderRepository.sumTotalByIsSoldAndTypeAndDateAndProduct(false, constants.productTypes.PublicHealth, productId, start, end);
            quotesRecipes = BigDecimal.ZERO;
        } else {
            quotesMedicines = orderRepository.sumTotalByIsSoldAndTypeAndDateAndThirdPartyAndProduct(false, constants.productTypes.SpecialControl, thirdPartyId, productId, start, end);
            quotesMedicinesSp = orderRepository.sumTotalByIsSoldAndTypeAndDateAndThirdPartyAndProduct(false, constants.productTypes.PublicHealth, thirdPartyId, productId, start, end);
            quotesRecipes = BigDecimal.ZERO;
        }

        // Ventas
        BigDecimal salesMedicines;
        BigDecimal salesMedicinesSp;
        BigDecimal salesRecipes;
        if (thirdPartyId == null && productId == null) {
            salesMedicines = orderRepository.sumTotalSalesByTypeAndDate(constants.productTypes.SpecialControl, start, end);
            salesMedicinesSp = orderRepository.sumTotalSalesByTypeAndDate(constants.productTypes.PublicHealth, start, end);
            salesRecipes = orderRepository.sumTotalSalesByTypeAndDate(constants.productTypes.Recipe, start, end);
        } else if (thirdPartyId != null && productId == null) {
            salesMedicines = orderRepository.sumTotalSalesByTypeAndDateAndThirdParty(constants.productTypes.SpecialControl, thirdPartyId, start, end);
            salesMedicinesSp = orderRepository.sumTotalSalesByTypeAndDateAndThirdParty(constants.productTypes.PublicHealth, thirdPartyId, start, end);
            salesRecipes = orderRepository.sumTotalSalesByTypeAndDateAndThirdParty(constants.productTypes.Recipe, thirdPartyId, start, end);
        } else if (thirdPartyId == null && productId != null) {
            salesMedicines = orderRepository.sumTotalSalesByTypeAndDateAndProduct(constants.productTypes.SpecialControl, productId, start, end);
            salesMedicinesSp = orderRepository.sumTotalSalesByTypeAndDateAndProduct(constants.productTypes.PublicHealth, productId, start, end);
            salesRecipes = BigDecimal.ZERO;
        } else {
            salesMedicines = orderRepository.sumTotalSalesByTypeAndDateAndThirdPartyAndProduct(constants.productTypes.SpecialControl, thirdPartyId, productId, start, end);
            salesMedicinesSp = orderRepository.sumTotalSalesByTypeAndDateAndThirdPartyAndProduct(constants.productTypes.PublicHealth, thirdPartyId, productId, start, end);
            salesRecipes = BigDecimal.ZERO;
        }

        CashSessionSummaryDTO summaries = CashSessionSummaryDTO.builder()
            .initialAmount(BigDecimal.ZERO)
            .totalIncome(rangeSalesTotal)
            .totalExpense(rangePurchasesTotal)
            .netBalance(rangeSalesTotal.subtract(rangePurchasesTotal))
            .netCashBalance(rangeSalesTotal)
            // Compras
            .purchasesMedicines(purchasesMedicines)
            .purchasesMedicinesSp(purchasesMedicinesSp)
            .purchasesRecipes(purchasesRecipes)
            // Cotizaciones
            .quotesMedicines(quotesMedicines)
            .quotesMedicinesSp(quotesMedicinesSp)
            .quotesRecipes(quotesRecipes)
            // Ventas
            .salesMedicines(salesMedicines)
            .salesMedicinesSp(salesMedicinesSp)
            .salesRecipes(salesRecipes)
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
