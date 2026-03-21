package com.aurealab.service.impl.CashRegister;

import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.dto.CashRegister.ThirdPartyRoleDTO;
import com.aurealab.mapper.CashRegister.ThirdPartyRoleMapper;
import com.aurealab.model.aurea.entity.RoleEntity;
import com.aurealab.model.cashRegister.entity.TPRoleEntity;
import com.aurealab.model.cashRegister.repository.ThirdPartyRoleRepository;
import com.aurealab.service.CashRegister.ThirdPartyRoleService;
import com.aurealab.service.impl.shared.TenantService;
import com.aurealab.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ThirdPartyRoleServiceImpl implements ThirdPartyRoleService{
    @Autowired
    private TenantService tenantService;

    @Autowired
    ThirdPartyRoleRepository thirdPartyRoleRepository;

    @Autowired
    JwtUtils jwtUtils;

    public Set<ThirdPartyRoleDTO> findRoleByRoleName(String role){
        Set<ThirdPartyRoleDTO> roleDTOS = new HashSet<>();

        Set<TPRoleEntity> roleEntity = tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            return thirdPartyRoleRepository.findByRoleName(role);
        });

        roleEntity.forEach(rol -> roleDTOS.add(ThirdPartyRoleMapper.toDto(rol)));

        return roleDTOS;

    }

    public Set<ThirdPartyRoleDTO> findAllEntitiesByIds(List<Long> ids) {
        return tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            Set<ThirdPartyRoleDTO> thirdPartyRole = new HashSet<>();

            List<TPRoleEntity> tpRoleEntities = thirdPartyRoleRepository.findAllById(ids);

            tpRoleEntities.forEach(tpRoleEntity ->
                    thirdPartyRole.add(ThirdPartyRoleMapper.toDto(tpRoleEntity))
            );
            return thirdPartyRole;
        });
    }

    public Set<ThirdPartyRoleDTO> findAll() {
        return tenantService.executeInTenant(jwtUtils.getCurrentTenant(), () -> {
            Set<ThirdPartyRoleDTO> thirdPartyRole = new HashSet<>();

            List<TPRoleEntity> tpRoleEntities = thirdPartyRoleRepository.findAll();

            tpRoleEntities.forEach(tpRoleEntity ->
                    thirdPartyRole.add(ThirdPartyRoleMapper.toDto(tpRoleEntity))
            );
            return thirdPartyRole;
        });
    }

}
