package com.aurealab.service.impl.inventory;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.OrderDTO;
import com.aurealab.dto.tables.OrderTableDTO;
import com.aurealab.mapper.inventory.OrderMapper;
import com.aurealab.model.inventory.entity.OrderEntity;
import com.aurealab.model.inventory.repository.OrderRepository;
import com.aurealab.model.specs.OrderSpecs;
import com.aurealab.service.Inventory.OrderService;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    public ResponseEntity<APIResponseDTO<String>> getOrders(int page, int size, String searchValue) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(APIResponseDTO.withPageable(constants.success.findedSuccess, constants.success.findedSuccess, findAllToTable(pageable, searchValue)));
    }

    public ResponseEntity<APIResponseDTO<OrderDTO>> getOrderById(Long id){
        OrderDTO response = findById(id);
        if(response == null) throw new RuntimeException(constants.messages.noData);
        return ResponseEntity.ok(APIResponseDTO.success(response, constants.success.findedSuccess));
    }

    public Page<OrderDTO> findAll(Pageable pageable, String searchValue) {
        Specification<OrderEntity> spec = OrderSpecs.search(searchValue);
        Page<OrderEntity> prescriptionInventoryEntities = orderRepository.findAll(spec, pageable);
        return prescriptionInventoryEntities.map(OrderMapper::toDto);
    }

    public Page<OrderTableDTO> findAllToTable(Pageable pageable, String searchValue) {
        Specification<OrderEntity> spec = OrderSpecs.search(searchValue);
        Page<OrderEntity> prescriptionInventoryEntities = orderRepository.findAll(spec, pageable);
        return prescriptionInventoryEntities.map(OrderMapper::toTableDto);
    }

    public OrderDTO findById(Long id) {
        Optional<OrderEntity> prescriptionInventory = orderRepository.findById(id);
        return prescriptionInventory.map(OrderMapper::toDto).orElse(null);
    }

}
