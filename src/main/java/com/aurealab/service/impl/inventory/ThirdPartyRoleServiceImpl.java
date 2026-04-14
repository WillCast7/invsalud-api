package com.aurealab.service.impl.inventory;

import com.aurealab.dto.CashRegister.ThirdPartyRoleDTO;
import com.aurealab.mapper.inventory.ThirdPartyRoleMapper;
import com.aurealab.model.inventory.entity.TPRoleEntity;
import com.aurealab.model.inventory.repository.ThirdPartyRoleRepository;
import com.aurealab.service.Inventory.ThirdPartyRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ThirdPartyRoleServiceImpl implements ThirdPartyRoleService{

    @Autowired
    ThirdPartyRoleRepository thirdPartyRoleRepository;

    public Set<ThirdPartyRoleDTO> findRoleByRoleName(String role){
        Set<ThirdPartyRoleDTO> roleDTOS = new HashSet<>();

        Set<TPRoleEntity> roleEntity = thirdPartyRoleRepository.findByRoleName(role);

        roleEntity.forEach(rol -> roleDTOS.add(ThirdPartyRoleMapper.toDto(rol)));

        return roleDTOS;

    }

    public Set<ThirdPartyRoleDTO> findAllEntitiesByIds(List<Long> ids) {
        Set<ThirdPartyRoleDTO> thirdPartyRole = new HashSet<>();

        List<TPRoleEntity> tpRoleEntities = thirdPartyRoleRepository.findAllById(ids);

        tpRoleEntities.forEach(tpRoleEntity ->
                thirdPartyRole.add(ThirdPartyRoleMapper.toDto(tpRoleEntity))
        );
            return thirdPartyRole;
    }

    public Set<ThirdPartyRoleDTO> findAll() {

        Set<ThirdPartyRoleDTO> thirdPartyRole = new HashSet<>();

        List<TPRoleEntity> tpRoleEntities = thirdPartyRoleRepository.findAll();

        tpRoleEntities.forEach(tpRoleEntity ->
                thirdPartyRole.add(ThirdPartyRoleMapper.toDto(tpRoleEntity))
        );
            return thirdPartyRole;

    }

}
