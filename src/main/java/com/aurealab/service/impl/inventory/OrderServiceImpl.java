package com.aurealab.service.impl.inventory;

import com.aurealab.dto.*;
import com.aurealab.dto.tables.OrderTableDTO;
import com.aurealab.mapper.inventory.OrderMapper;
import com.aurealab.model.inventory.entity.*;
import com.aurealab.model.inventory.repository.OrderRepository;
import com.aurealab.model.specs.OrderSpecs;
import com.aurealab.service.Inventory.*;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    DocumentSequenceService documentSequenceService;

    @Autowired
    ThirdPartyService thirdPartyService;

    @Autowired
    PrescriptionInventoryService prescriptionInventoryService;

    @Autowired
    RecipeInventoryService recipeInventoryService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    com.aurealab.service.notification.NotificationService notificationService;

    public ResponseEntity<APIResponseDTO<String>> getOrders(int page, int size, String searchValue, boolean isSold, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(APIResponseDTO.withPageable(constants.success.findedSuccess, constants.success.findedSuccess, findAllToTable(pageable, searchValue, isSold, type)));
    }

    @Transactional
    public ResponseEntity<APIResponseDTO<OrderDTO>> getOrderById(Long id){
        OrderDTO response = findById(id);
        if(response == null) throw new RuntimeException(constants.messages.noData);
        return ResponseEntity.ok(APIResponseDTO.success(response, constants.success.findedSuccess));
    }

    public Page<OrderDTO> findAll(Pageable pageable, String searchValue, boolean isSold, String type) {
        Specification<OrderEntity> spec = OrderSpecs.search(searchValue, isSold, type);
        Page<OrderEntity> prescriptionInventoryEntities = orderRepository.findAll(spec, pageable);
        return prescriptionInventoryEntities.map(OrderMapper::toDto);
    }

    public Page<OrderTableDTO> findAllToTable(Pageable pageable, String searchValue, boolean isSold, String type) {
        Specification<OrderEntity> spec = OrderSpecs.search(searchValue, isSold, type);
        Page<OrderEntity> prescriptionInventoryEntities = orderRepository.findAll(spec, pageable);
        return prescriptionInventoryEntities.map(OrderMapper::toTableDto);
    }

    @Transactional
    public OrderDTO findById(Long id) {
        Optional<OrderEntity> prescriptionInventory = orderRepository.findById(id);
        return prescriptionInventory.map(OrderMapper::toDto).orElse(null);
    }

    @Override
    @Transactional
    public ResponseEntity<APIResponseDTO<OrderDTO>> saveOrder(OrderRequestDTO request) {
        OrderEntity order = new OrderEntity();

        ThirdPartyEntity thirdParty = thirdPartyService.findThirdPartyEntityById(request.thirdParty());

        order.setThirdParty(thirdParty);
        order.setTotal(request.total());
        order.setType(request.type());
        order.setStatus("PENDING");
        order.setActive(true);
        order.setSold(false);
        order.setExpirateAt(LocalDateTime.now().plusDays(15));
        order.setCreatedBy(jwtUtils.getCurrentUserId());

        // Generar código
        String prefix = switch (request.type()) {
            case constants.productTypes.PublicHealth -> constants.configParam.orderPublicHealthPrefix;
            case constants.productTypes.SpecialControl -> constants.configParam.orderEspecialControlPrefix;
            default -> constants.configParam.orderRecipePrefix;
        };

        order.setOrderCode(documentSequenceService.getNextInvoiceNumber(prefix));

        List<OrderItemEntity> items = new ArrayList<>();

        int ivaVal = request.iva() != null ? request.iva() : 0;
        BigDecimal priceIvaVal = request.priceIva() != null ? request.priceIva() : BigDecimal.ZERO;
        order.setIva(ivaVal);
        order.setPriceIva(priceIvaVal);

        if(prefix.equals(constants.configParam.orderRecipePrefix)) {

            RecipeInventoryEntity recipeInventory = recipeInventoryService.findByIdEntity();
            BigDecimal unitPrice = recipeInventory.getPrice();
            BigDecimal subtotalVal = request.subtotal() != null ? request.subtotal() : BigDecimal.valueOf(request.units()).multiply(unitPrice);
            if (request.priceIva() == null && ivaVal > 0) {
                priceIvaVal = subtotalVal.multiply(BigDecimal.valueOf(ivaVal)).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                order.setPriceIva(priceIvaVal);
            }
            BigDecimal totalVal = request.total() != null ? request.total() : subtotalVal.add(priceIvaVal);

            order.setSubtotal(subtotalVal);
            order.setTotal(totalVal);

            items.add(OrderItemEntity.builder()
                .order(order)
                .priceUnit(unitPrice)
                .units(request.units())
                .priceTotal(totalVal)
                .build()
            );

            updateRecipeInventory(Math.toIntExact(request.units()), "order");

        }else {
            BigDecimal subtotalVal = request.subtotal() != null ? request.subtotal() : request.total();
            order.setSubtotal(subtotalVal);

            if (request.items() != null) {
                for (OrderItemRequestDTO itemDto : request.items()) {
                    OrderItemEntity item = new OrderItemEntity();
                    PrescriptionInventoryEntity inventory = prescriptionInventoryService.findByIdEntity(itemDto.product());

                    if (inventory.getAvailableUnits() < itemDto.units()) {
                        throw new RuntimeException("No hay suficientes unidades para el producto seleccionado.");
                    }

                    // Descontar inventario
                    inventory.setAvailableUnits((int) (inventory.getAvailableUnits() - itemDto.units()));

                    // Verificar alerta de stock mínimo según product.unitsAlert
                    checkAndTriggerLowStockAlert(inventory);

                    item.setOrder(order);
                    item.setInventory(inventory);
                    item.setPriceUnit(itemDto.priceUnit());
                    item.setUnits(itemDto.units());
                    item.setPriceTotal(itemDto.priceTotal());
                    items.add(item);
                }
            }
        }
        order.setItems(items);


        OrderEntity savedOrder = orderRepository.save(order);
        return ResponseEntity.ok(APIResponseDTO.success(OrderMapper.toDto(savedOrder), constants.success.savedSuccess));
    }

    @Override
    @Transactional
    public ResponseEntity<APIResponseDTO<OrderDTO>> savePublicSale(OrderRequestDTO request) {
        OrderEntity order = new OrderEntity();

        ThirdPartyEntity thirdParty = thirdPartyService.findThirdPartyEntityById(request.thirdParty());

        order.setThirdParty(thirdParty);
        order.setTotal(request.total());
        order.setSubtotal(request.subtotal() != null ? request.subtotal() : request.total());
        order.setType(request.type());
        order.setStatus("SOLD");
        order.setSold(true);
        order.setSoldAt(LocalDateTime.now());
        order.setExpirateAt(LocalDateTime.now().plusDays(15));
        order.setCreatedBy(jwtUtils.getCurrentUserId());
        order.setIva(request.iva() != null ? request.iva() : 0);
        order.setPriceIva(request.priceIva() != null ? request.priceIva() : BigDecimal.ZERO);

        // Generar código de salida/venta
        String prefix = switch (request.type()) {
            case constants.productTypes.PublicHealth -> constants.configParam.salePublicHealthPrefix;
            case constants.productTypes.SpecialControl -> constants.configParam.saleEspecialControlPrefix;
            default -> constants.configParam.saleRecipePrefix;
        };

        String code = documentSequenceService.getNextInvoiceNumber(prefix);
        order.setSoldCode(code);
        order.setOrderCode(code); // Asignamos a ambos para evitar nulos en búsquedas

        // Mapear items
        List<OrderItemEntity> items = new ArrayList<>();
        if (request.items() != null) {
            for (OrderItemRequestDTO itemDto : request.items()) {
                OrderItemEntity item = new OrderItemEntity();
                PrescriptionInventoryEntity inventory = prescriptionInventoryService.findByIdEntity(itemDto.product());

                if (inventory.getAvailableUnits() < itemDto.units()) {
                    throw new RuntimeException("No hay suficientes unidades para el producto seleccionado.");
                }

                // Descontar inventario (disponible y físico)
                inventory.setAvailableUnits((int) (inventory.getAvailableUnits() - itemDto.units()));
                inventory.setTotalUnits((int) (inventory.getTotalUnits() - itemDto.units()));

                // Verificar alerta de stock mínimo según product.unitsAlert
                checkAndTriggerLowStockAlert(inventory);

                item.setOrder(order);
                item.setInventory(inventory);
                item.setPriceUnit(itemDto.priceUnit());
                item.setUnits(itemDto.units());
                item.setPriceTotal(itemDto.priceTotal());
                items.add(item);
            }
        }
        order.setItems(items);

        OrderEntity savedOrder = orderRepository.save(order);
        return ResponseEntity.ok(APIResponseDTO.success(OrderMapper.toDto(savedOrder), constants.success.savedSuccess));
    }


    @Override
    @Transactional
    public ResponseEntity<APIResponseDTO<OrderDTO>> abortOrder(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(constants.messages.dontFoundByID));

        if (!order.getStatus().equalsIgnoreCase("PENDING")) {
            throw new RuntimeException("La cotización no está en estado PENDIENTE y no puede ser anulada");
        }

        order.setActive(false);
        order.setStatus("CANCELLED");

        // Devolver inventario reservado
        if (order.getItems() != null) {
            for (OrderItemEntity itemEntity : order.getItems()) {
                PrescriptionInventoryEntity inventory = itemEntity.getInventory();
                inventory.setAvailableUnits((int) (inventory.getAvailableUnits() + itemEntity.getUnits()));
            }
        }

        OrderEntity savedOrder = orderRepository.save(order);

        return ResponseEntity.ok(APIResponseDTO.success(OrderMapper.toDto(savedOrder), constants.success.updatedSuccess));
    }

    @Override
    @Transactional
    public ResponseEntity<APIResponseDTO<OrderDTO>> sellOrder(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(constants.messages.dontFoundByID));

        if (!order.getStatus().equalsIgnoreCase("PENDING")) {
            throw new RuntimeException("La cotización no está en estado PENDIENTE y no puede ser vendida");
        }

        order.setStatus("SOLD");
        order.setSold(true);
        order.setSoldAt(LocalDateTime.now());

        // Generar código de salida/venta
        String prefix = switch (order.getType()) {
            case constants.productTypes.PublicHealth -> constants.configParam.salePublicHealthPrefix;
            case constants.productTypes.SpecialControl -> constants.configParam.saleEspecialControlPrefix;
            default -> constants.configParam.saleRecipePrefix;
        };
        order.setSoldCode(documentSequenceService.getNextInvoiceNumber(prefix));

        // Descontar inventario físico
        if (order.getItems() != null) {
            for (OrderItemEntity itemEntity : order.getItems()) {
                PrescriptionInventoryEntity inventory = itemEntity.getInventory();

                if (inventory.getTotalUnits() < itemEntity.getUnits()) {
                    throw new RuntimeException("No hay suficientes unidades físicas para el producto: " + inventory.getProduct().getName());
                }

                inventory.setTotalUnits((int) (inventory.getTotalUnits() - itemEntity.getUnits()));
            }
        }

        OrderEntity savedOrder = orderRepository.save(order);

        return ResponseEntity.ok(APIResponseDTO.success(OrderMapper.toDto(savedOrder), constants.success.updatedSuccess));
    }

    @Override
    @Transactional
    public ResponseEntity<APIResponseDTO<OrderDTO>> sellOrderRecipe(Long id, SellRecipeRequestDTO request) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(constants.messages.dontFoundByID));

        if (!order.getStatus().equalsIgnoreCase("PENDING")) {
            throw new RuntimeException("La cotización no está en estado PENDIENTE y no puede ser vendida");
        }

        order.setStatus("SOLD");
        order.setSold(true);
        order.setSoldAt(LocalDateTime.now());

        String prefix = constants.configParam.saleRecipePrefix;
        order.setSoldCode(documentSequenceService.getNextInvoiceNumber(prefix));

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (OrderItemEntity itemEntity : order.getItems()) {
                itemEntity.setStartSerialSold(request.initialSerial());
                itemEntity.setEndSerialSold(request.finalSerial());
            }
        }

        OrderEntity savedOrder = orderRepository.save(order);
        updateRecipeInventory(Math.toIntExact(order.getItems().get(0).getUnits()), "sale");
        return ResponseEntity.ok(APIResponseDTO.success(OrderMapper.toDto(savedOrder), constants.success.updatedSuccess));
    }

    private void updateRecipeInventory(int incomingUnits, String type) {

        RecipeInventoryEntity current = recipeInventoryService.findByIdEntity();

        if (Objects.equals(type, "sale")) {
            if (current.getTotalUnits() < incomingUnits) {
                throw new RuntimeException("No hay suficientes unidades.");
            }
            current.setTotalUnits(current.getTotalUnits() - incomingUnits);
        } else {
            if (current.getAvaliableUnits() < incomingUnits) {
                throw new RuntimeException("No hay suficientes unidades.");
            }
            current.setAvaliableUnits(current.getAvaliableUnits() - incomingUnits);
        }
        recipeInventoryService.save(current);
    }

    @Override
    @Transactional
    public Page<PrescriptionInventoryTableDTO> getOrdersReport(
            int page, int size, Boolean isSold, String type,
            LocalDateTime start, LocalDateTime end,
            String documentNumber, String product, String batch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Specification<OrderEntity> spec = OrderSpecs.reportSearch(isSold, type, start, end, documentNumber, product, batch);
        Page<OrderEntity> ordersPage = orderRepository.findAll(spec, pageable);

        List<PrescriptionInventoryTableDTO> dtoList = new ArrayList<>();
        for (OrderEntity order : ordersPage.getContent()) {
            if (order.getItems() != null) {
                for (OrderItemEntity item : order.getItems()) {
                    PrescriptionInventoryTableDTO dto = mapOrderItemToTableDto(order, item);
                    dtoList.add(dto);
                }
            }
        }

        return new org.springframework.data.domain.PageImpl<>(dtoList, pageable, ordersPage.getTotalElements());
    }

    private PrescriptionInventoryTableDTO mapOrderItemToTableDto(OrderEntity order, OrderItemEntity item) {
        if (item.getInventory() != null) {
            return PrescriptionInventoryTableDTO.builder()
                    .id(item.getId())
                    .product(item.getInventory().getProduct().getName())
                    .presentation(item.getInventory().getProduct().getPresentation())
                    .pharmaceuticalForm(item.getInventory().getProduct().getPharmaceuticalForm())
                    .batch(item.getInventory().getBatch().getCode())
                    .purchasePrice(item.getInventory().getPurchasePrice())
                    .salePrice(item.getPriceUnit())
                    .totalUnits(item.getUnits())
                    .availableUnits((long) item.getInventory().getAvailableUnits())
                    .expirationDate(item.getInventory().getExpirationDate())
                    .isActive(order.isActive())
                    .build();
        } else {
            return PrescriptionInventoryTableDTO.builder()
                    .id(item.getId())
                    .product("Recetarios")
                    .presentation("N/A")
                    .pharmaceuticalForm("N/A")
                    .batch("N/A")
                    .purchasePrice(BigDecimal.ZERO)
                    .salePrice(item.getPriceUnit())
                    .totalUnits(item.getUnits())
                    .availableUnits(0L)
                    .expirationDate(null)
                    .isActive(order.isActive())
                    .build();
        }
    }

    private void checkAndTriggerLowStockAlert(PrescriptionInventoryEntity inventory) {
        if (inventory == null || inventory.getProduct() == null) return;

        ProductEntity product = inventory.getProduct();
        Integer unitsAlert = product.getUnitsAlert();
        int availableUnits = inventory.getAvailableUnits();

        // Disparar alerta si el umbral está configurado y el stock disponible es menor o igual
        if (unitsAlert != null && unitsAlert > 0 && availableUnits <= unitsAlert) {
            try {
                String productName = product.getName() != null ? product.getName() : "Medicamento";
                String productCode = product.getCode() != null ? product.getCode() : "";
                String batchCode = (inventory.getBatch() != null && inventory.getBatch().getCode() != null)
                        ? inventory.getBatch().getCode() : "N/A";

                String title = "⚠️ Stock Bajo: " + productName;
                String message = String.format(
                        "El medicamento %s (Código: %s, Lote: %s) ha alcanzado el nivel de alerta. Quedan %d unidades disponibles (Umbral configurado: %d).",
                        productName, productCode, batchCode, availableUnits, unitsAlert
                );
                String priority = availableUnits == 0 ? "CRITICAL" : "WARNING";

                notificationService.createNotification(com.aurealab.dto.notification.CreateNotificationRequestDTO.builder()
                        .title(title)
                        .message(message)
                        .category("INVENTORY_ALERT")
                        .priority(priority)
                        .targetUrl("/inventory/medicines")
                        .userIds(null) // Notificar a todos los usuarios
                        .build());
            } catch (Exception e) {
                // Prevenir que un fallo en el envío de notificación interrumpa la transacción de la orden
                e.printStackTrace();
            }
        }
    }
}
