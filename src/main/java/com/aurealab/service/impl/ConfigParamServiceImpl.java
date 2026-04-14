package com.aurealab.service.impl;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.ConfigParamDTO;
import com.aurealab.dto.response.ThirdPartyWithParamsResponseDTO;
import com.aurealab.dto.response.UserWithParamsResponseDTO;
import com.aurealab.mapper.ConfigParamsMapper;
import com.aurealab.model.aurea.entity.ConfigParamsEntity;
import com.aurealab.model.aurea.repository.ConfigParamsRepository;
import com.aurealab.model.specs.ConfigParamSpecs;
import com.aurealab.service.Inventory.ThirdPartyRoleService;
import com.aurealab.service.ConfigParamService;
import com.aurealab.service.RoleService;
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
    RoleService roleService;

    public Set<ConfigParamDTO> getConfigParamsByParent(String parent){
        try{
            if(parent == null){
                log.error("El parametro no deberia estar vacio");
                return null;
            }

            Set<ConfigParamsEntity> configParamsEntitySet = configParamsRepository.findByParent(parent);
            Set<ConfigParamDTO> configParamDTOSet = new HashSet<>();
            configParamsEntitySet.forEach(configParamEntity->configParamDTOSet.add(ConfigParamsMapper.toDto(configParamEntity)));

            return configParamDTOSet;

        }catch (Exception e){
            log.error("Error leyendo los parametros de configuracion de " + parent, e);
            throw new BaseException(constants.errors.findError, constants.descriptions.configParams, e) {};
        }

    }

    public ResponseEntity<APIResponseDTO<String>> getConfigParams(int page, int size, String searchValue){
        try{
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

            return ResponseEntity.ok(APIResponseDTO.withPageable(constants.success.findedSuccess, constants.success.findedSuccess, findAll(pageable, searchValue)));

        }catch (Exception e){
            log.error("Error leyendo los parametros de configuracion: ", e);
            throw new BaseException(constants.errors.findError, constants.descriptions.configParams, e) {};
        }

    }

    public ResponseEntity<APIResponseDTO<ThirdPartyWithParamsResponseDTO>> getCreatingCustomerParams(){
        return ResponseEntity.ok(APIResponseDTO.success(findCreatingThirdPartyParams(), constants.success.findedSuccess));
    }

    public ResponseEntity<APIResponseDTO<UserWithParamsResponseDTO>> getCreatingUserParams(){
        return ResponseEntity.ok(APIResponseDTO.success(findCreatingUserParams(), constants.success.findedSuccess));
    }

    public UserWithParamsResponseDTO findCreatingUserParams(){
        return UserWithParamsResponseDTO.builder()
                .documentTypes(getConfigParamsByParent("documentType"))
                .roles(roleService.getAllRoles())
                .build();
    }

    public ResponseEntity<APIResponseDTO<Set<ConfigParamDTO>>> searchParamsByParent(String parent){
        return ResponseEntity.ok(
                APIResponseDTO.success(
                        getConfigParamsByParent(parent),
                        constants.success.findedSuccess
                )
        );
    }

    public Set<ConfigParamDTO> findParamsByParent(String parent){
        return getConfigParamsByParent(parent);
    }

    public ThirdPartyWithParamsResponseDTO findCreatingThirdPartyParams(){
        return ThirdPartyWithParamsResponseDTO.builder()
                        .documentTypes(getConfigParamsByParent("documentType"))
                        .roles(thirdPartyRoleService.findAll())
                        .build();
    }

    public Page<ConfigParamDTO> findAll(Pageable pageable, String searchValue){
        Specification<ConfigParamsEntity> specs = ConfigParamSpecs.search(searchValue);

        Page<ConfigParamsEntity> configParamEntity = configParamsRepository.findAll(specs, pageable);
        return configParamEntity.map(ConfigParamsMapper::toDto);
    }

}
