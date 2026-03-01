package com.aurealab.service.impl;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.ConfigParamDTO;
import com.aurealab.dto.response.ThirdPartyWithParamsResponseDTO;
import com.aurealab.dto.response.UserWithParamsResponseDTO;
import com.aurealab.model.aurea.entity.ConfigParamsEntity;
import com.aurealab.model.aurea.repository.ConfigParamsRepository;
import com.aurealab.service.CashRegister.ThirdPartyRoleService;
import com.aurealab.service.ConfigParamService;
import com.aurealab.service.RoleService;
import com.aurealab.util.constants;
import com.aurealab.util.exceptions.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
            configParamsEntitySet.forEach(configParamEntity->configParamDTOSet.add(setConfigParamsEntityToDTO(configParamEntity)));

            return configParamDTOSet;

        }catch (Exception e){
            log.error("Error leyendo los parametros de configuracion de " + parent, e);
            throw new BaseException(constants.errors.findError, constants.descriptions.configParams, e) {};
        }

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
        return UserWithParamsResponseDTO.builder()
                .documentTypes(getConfigParamsByParent("documentType"))
                .roles(roleService.getAllRoles())
                .build();
    }

    public ThirdPartyWithParamsResponseDTO findCreatingThirdPartyParams(){
        return ThirdPartyWithParamsResponseDTO.builder()
                        .documentTypes(getConfigParamsByParent("documentType"))
                        .roles(thirdPartyRoleService.findAll())
                        .build();
    }

}
