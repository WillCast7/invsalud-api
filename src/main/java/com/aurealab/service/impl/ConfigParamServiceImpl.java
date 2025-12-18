package com.aurealab.service.impl;

import com.aurealab.dto.ConfigParamDTO;
import com.aurealab.model.aurea.entity.ConfigParamsEntity;
import com.aurealab.model.aurea.repository.ConfigParamsRepository;
import com.aurealab.service.ConfigParamService;
import com.aurealab.util.constants;
import com.aurealab.util.exceptions.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class ConfigParamServiceImpl implements ConfigParamService {

    @Autowired
    ConfigParamsRepository configParamsRepository;

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

}
