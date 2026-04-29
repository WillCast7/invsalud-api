package com.aurealab.service.impl.inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.OrderDTO;
import com.aurealab.dto.tables.OrderTableDTO;
import com.aurealab.mapper.inventory.OrderMapper;
import com.aurealab.model.inventory.entity.OrderEntity;
import com.aurealab.model.inventory.repository.OrderRepository;
import com.aurealab.model.specs.OrderSpecs;
import com.aurealab.service.Inventory.OrderService;
import com.aurealab.dto.OrderRequestDTO;
import com.aurealab.dto.OrderItemRequestDTO;
import com.aurealab.model.inventory.entity.OrderItemEntity;
import com.aurealab.model.inventory.entity.PrescriptionInventoryEntity;
import com.aurealab.model.inventory.entity.ThirdPartyEntity;
import com.aurealab.service.Inventory.DocumentSequenceService;
import com.aurealab.service.Inventory.PrescriptionInventoryService;
import com.aurealab.service.Inventory.ThirdPartyService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    JwtUtils jwtUtils;

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

        // Mapear items
        List<OrderItemEntity> items = new ArrayList<>();
        if (request.items() != null) {
            for (OrderItemRequestDTO itemDto : request.items()) {
                OrderItemEntity item = new OrderItemEntity();
                PrescriptionInventoryEntity inventory = prescriptionInventoryService.findByIdEntity(itemDto.product());

                if (inventory.getAvailableUnits() < itemDto.units()) {
                    throw new RuntimeException("No hay suficientes unidades para el producto seleccionado.");
                }

                // Descontar inventario
                inventory.setAvailableUnits((int) (inventory.getAvailableUnits() - itemDto.units()));


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
    public ResponseEntity<APIResponseDTO<OrderDTO>> savePublicSale(OrderRequestDTO request) {
        OrderEntity order = new OrderEntity();

        ThirdPartyEntity thirdParty = thirdPartyService.findThirdPartyEntityById(request.thirdParty());

        order.setThirdParty(thirdParty);
        order.setTotal(request.total());
        order.setType(request.type());
        order.setStatus("SOLD");
        order.setSold(true);
        order.setSoldAt(LocalDateTime.now());
        order.setExpirateAt(LocalDateTime.now().plusDays(15));
        order.setCreatedBy(jwtUtils.getCurrentUserId());

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

}
