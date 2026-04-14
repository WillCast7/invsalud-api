package com.aurealab.service.impl.management;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.ConfigParamDTO;
import com.aurealab.dto.response.ThirdPartyWithParamsResponseDTO;
import com.aurealab.dto.response.UserWithParamsResponseDTO;
import com.aurealab.mapper.ConfigParamsMapper;
import com.aurealab.model.management.entity.ConfigParamsEntity;
import com.aurealab.model.management.repository.ConfigParamsRepository;
import com.aurealab.model.specs.ConfigParamSpecs;
import com.aurealab.service.ProductService;
import com.aurealab.service.ThirdPartyRoleService;
import com.aurealab.service.impl.shared.TenantService;
import com.aurealab.service.management.ConfigParamService;
import com.aurealab.service.RoleService;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import com.aurealab.util.exceptions.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class ConfigParamServiceImpl implements ConfigParamService {

    @Autowired
    ConfigParamsRepository configParamsRepository;

    @Autowired
    ThirdPartyRoleService thirdPartyRoleService;

    @Autowired
    ProductService productService;

    @Autowired
    RoleService roleService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private TenantService tenantService;

    public ResponseEntity<APIResponseDTO<String>> getConfigParams(int page, int size, String searchValue){
        try{
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

            return ResponseEntity.ok(APIResponseDTO.withPageable(constants.success.findedSuccess, constants.success.findedSuccess, findAll(pageable, searchValue)));

        }catch (Exception e){
            log.error("Error leyendo los parametros de configuracion: ", e);
            throw new BaseException(constants.errors.findError, constants.descriptions.configParams, e) {};
        }

    }

    public Set<ConfigParamDTO> getConfigParamsByParent(String parent){
        return tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            try {
                if (parent == null) {
                    log.error("El parametro no deberia estar vacio");
                    return null;
                }

                Set<ConfigParamsEntity> configParamsEntitySet = configParamsRepository.findByParent(parent);
                Set<ConfigParamDTO> configParamDTOSet = new HashSet<>();
                configParamsEntitySet.forEach(configParamEntity -> configParamDTOSet.add(setConfigParamsEntityToDTO(configParamEntity)));

                return configParamDTOSet;

            } catch (Exception e) {
                log.error("Error leyendo los parametros de configuracion de " + parent, e);
                throw new BaseException(constants.errors.findError, constants.descriptions.configParams, e) {
                };
            }
        });

    }

    public ConfigParamDTO setConfigParamsEntityToDTO(ConfigParamsEntity configParamsEntity){
        return ConfigParamDTO.builder()
                .id(configParamsEntity.getId())
                .name(configParamsEntity.getName())
                .shortname(configParamsEntity.getShortname())
                .build();
    }

    public ResponseEntity<APIResponseDTO<ThirdPartyWithParamsResponseDTO>> getCreatingCustomerParams(){

        return ResponseEntity.ok(APIResponseDTO.success(findCreatingThirdPartyParams(), constants.success.findedSuccess));
    }

    public ResponseEntity<APIResponseDTO<UserWithParamsResponseDTO>> getCreatingUserParams(){
        return ResponseEntity.ok(APIResponseDTO.success(findCreatingUserParams(), constants.success.findedSuccess));
    }

    public UserWithParamsResponseDTO findCreatingUserParams(){
        return tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            return UserWithParamsResponseDTO.builder()
                    .documentTypes(getConfigParamsByParent("documentType"))
                    .roles(roleService.getAllRoles())
                    .build();
        });
    }

    public ThirdPartyWithParamsResponseDTO findCreatingThirdPartyParams(){
        return tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            return ThirdPartyWithParamsResponseDTO.builder()
                    .documentTypes(getConfigParamsByParent("documentType"))
                    .roles(thirdPartyRoleService.findAll())
                    .products(productService.findAllProductsByCategory(2L))
                    .build();
        });
    }

    public Page<ConfigParamDTO> findAll(Pageable pageable, String searchValue){
        return tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            Specification<ConfigParamsEntity> specs = ConfigParamSpecs.search(searchValue);

            Page<ConfigParamsEntity> configParamEntity = configParamsRepository.findAll(specs, pageable);
            return configParamEntity.map(ConfigParamsMapper::toDto);
        });
    }
}