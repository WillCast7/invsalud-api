package com.aurealab.service.impl.inventory;

import com.aurealab.dto.*;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.dto.CashRegister.request.PurchasingRequestDTO;
import com.aurealab.dto.tables.PurchasingTableDTO;
import com.aurealab.mapper.inventory.PrescriptionInventoryMapper;
import com.aurealab.mapper.inventory.PurchasingItemMapper;
import com.aurealab.mapper.inventory.PurchasingMapper;
import com.aurealab.model.inventory.entity.PurchasingEntity;
import com.aurealab.model.inventory.entity.PurchasingItemEntity;
import com.aurealab.model.inventory.entity.PurchasingRecipeEntity;
import com.aurealab.model.inventory.entity.RecipeInventoryEntity;
import com.aurealab.model.inventory.repository.PurchasingRepository;
import com.aurealab.model.specs.PurchasingSpecs;
import com.aurealab.service.Inventory.*;
import com.aurealab.service.UserService;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.aurealab.util.JwtUtils;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PurchasingServiceImpl implements PurchasingService {

    @Autowired
    PurchasingRepository purchasingRepository;

    @Autowired
    DocumentSequenceService documentSequenceService;

    @Autowired
    PrescriptionInventoryService  prescriptionInventoryService;

    @Autowired
    ThirdPartyService thirdPartyService;

    @Autowired
    RecipeInventoryService recipeInventoryService;

    @Autowired
    PurchasingRecipeService  purchasingRecipeService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    public ResponseEntity<APIResponseDTO<String>> getPurchasing(int page, int size, String searchValue, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(APIResponseDTO.withPageable(constants.success.findedSuccess, constants.success.findedSuccess, findAllToTable(pageable, searchValue, type)));
    }

    public ResponseEntity<APIResponseDTO<PurchasingDTO>> getPurchasingById(Long id){
        PurchasingDTO response = findById(id);
        System.out.println("neeega");
        if(response == null) throw new RuntimeException(constants.messages.noData);
        return ResponseEntity.ok(APIResponseDTO.success(response, constants.success.findedSuccess));
    }

    public Page<PurchasingDTO> findAll(Pageable pageable, String searchValue) {
        Specification<PurchasingEntity> spec = PurchasingSpecs.search(searchValue, "");
        Page<PurchasingEntity> prescriptionInventoryEntities = purchasingRepository.findAll(spec, pageable);
        return prescriptionInventoryEntities.map(PurchasingMapper::toDto);
    }

    public Page<PurchasingTableDTO> findAllToTable(Pageable pageable, String searchValue, String type) {
        Specification<PurchasingEntity> spec = PurchasingSpecs.search(searchValue, type);
        Page<PurchasingEntity> prescriptionInventoryEntities = purchasingRepository.findAll(spec, pageable);
        return prescriptionInventoryEntities.map(PurchasingMapper::toTableDto);
    }

    @Transactional
    public PurchasingDTO findById(Long id) {
        Optional<PurchasingEntity> prescriptionInventory = purchasingRepository.findById(id);
        System.out.println("neewega");
        return prescriptionInventory.map(PurchasingMapper::toDto).orElse(null);
    }

    @Transactional
    public ResponseEntity<APIResponseDTO<String>> savePurchasing(PurchasingRequestDTO purchasingDTO) {
        // 1. Identificar el prefijo y obtener datos del contexto
        String productType = switch (purchasingDTO.type()) {
            case constants.productTypes.PublicHealth -> constants.configParam.incomePublicHealthPrefix;
            case constants.productTypes.Recipe -> constants.configParam.incomeRecipePrefix;
            default -> constants.configParam.incomeEspecialControlPrefix;
        };

        PersonDTO personCreator = userService.getUserById(jwtUtils.getCurrentUserId()).getPerson();
        String creatorFullName = personCreator.getNames() + " " + personCreator.getSurnames();

        // 2. Preparar el contenedor principal (La Entidad Padre)
        // Usamos el builder para preparar la cabecera de la compra
        PurchasingEntity entityToSave = new PurchasingEntity();
        entityToSave.setThirdParty(thirdPartyService.findThirdPartyEntityById(purchasingDTO.thirdParty()));
        entityToSave.setTotal(purchasingDTO.total());
        entityToSave.setType(purchasingDTO.type());
        entityToSave.setObservations(purchasingDTO.observations());
        entityToSave.setCreatedBy(jwtUtils.getCurrentUserId());
        entityToSave.setPurchasedBy(creatorFullName);
        entityToSave.setPurchasedCode(documentSequenceService.getNextInvoiceNumber(productType));
        entityToSave.setIsActive(true);


        // 3. Lógica de Filtrado por Tipo (Preparación de Hijos)
        if (Objects.equals(purchasingDTO.type(), constants.productTypes.Recipe)) {

            // --- FLUJO A: RECETARIOS ---
            if (purchasingDTO.recipe() == null) throw new RuntimeException("Faltan datos del recetario.");

            // Preparar el detalle de la receta
            PurchasingRecipeEntity recipeDetail = new PurchasingRecipeEntity();
            recipeDetail.setPurchasing(entityToSave); // Vínculo bidireccional
            recipeDetail.setUnits(purchasingDTO.recipe().units());
            recipeDetail.setPriceUnit(purchasingDTO.recipe().priceUnit());
            recipeDetail.setPriceTotal(purchasingDTO.recipe().priceTotal());
            System.out.println("inicio del proceso");

            entityToSave.setPurchasingRecipe(recipeDetail);
            entityToSave.setItems(new ArrayList<>()); // Recetas no suelen llevar items de inventario individual
            System.out.println("inicio del proceso");

            // Actualizar Inventario de Folios (Esto sí debe ser una operación aparte o al final)
            updateRecipeInventory(purchasingDTO.recipe().units());

        } else {

            // --- FLUJO B: MEDICAMENTOS (Items) ---
            if (purchasingDTO.items() == null || purchasingDTO.items().isEmpty()) {
                throw new RuntimeException("La lista de ítems es obligatoria.");
            }

            List<PurchasingItemEntity> items = new ArrayList<>();
            purchasingDTO.items().forEach(itemDto -> {
                PurchasingItemEntity itemEntity = PurchasingItemMapper.toEntity(itemDto);
                itemEntity.setPurchasing(entityToSave); // Vínculo bidireccional
                items.add(itemEntity);
            });

            entityToSave.setItems(items);
            entityToSave.setPurchasingRecipe(null);
        }

        System.out.println("pre guardar");
        PurchasingEntity savedEntity = purchasingRepository.save(entityToSave);
        System.out.println("post guardar");

        // 5. Post-Procesamiento (Solo para inventario individual si no es receta)
        if (!Objects.equals(savedEntity.getType(), constants.productTypes.Recipe)) {
            processInventory(savedEntity);
        }

        return ResponseEntity.ok(APIResponseDTO.success(constants.success.savedSuccess, constants.success.savedSuccess));
    }

    // Métodos de apoyo para mantener el código limpio
    private void updateRecipeInventory(int incomingUnits) {
        System.out.println("entro al update");

        RecipeInventoryEntity current = recipeInventoryService.findById();

        BigDecimal units = BigDecimal.valueOf(incomingUnits);

        RecipeInventoryEntity updated = RecipeInventoryEntity.builder()
                .id(1L)
                .totalUnits(current.getTotalUnits() + incomingUnits)
                .avaliableUnits(current.getAvaliableUnits().add(units))
                .price(current.getPrice())
                .build();
        recipeInventoryService.save(updated);
    }

    private void processInventory(PurchasingEntity savedEntity) {
        PurchasingDTO savedDto = PurchasingMapper.toDto(savedEntity);
        List<PrescriptionInventoryDTO> inventories = PrescriptionInventoryMapper.fromPurchasing(savedDto);
        for (PrescriptionInventoryDTO inv : inventories) {
            prescriptionInventoryService.processPrescriptionInventory(inv);
        }
    }

    public PurchasingEntity savePurchasingEntity(PurchasingEntity purchasingEntity){
        return purchasingRepository.save(purchasingEntity);
    }
}
