package com.aurealab.service.impl.CashRegister;

import com.aurealab.dto.CashRegister.ThirdPartyRoleDTO;
import com.aurealab.mapper.CashRegister.ThirdPartyRoleMapper;
import com.aurealab.model.cashRegister.entity.TPRoleEntity;
import com.aurealab.model.cashRegister.repository.ThirdPartyRoleRepository;
import com.aurealab.service.CashRegister.ThirdPartyRoleService;
import com.aurealab.service.impl.shared.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ThirdPartyRoleServiceImpl implements ThirdPartyRoleService{
    @Autowired
    private TenantService tenantService;

    @Autowired
    ThirdPartyRoleRepository thirdPartyRoleRepository;

    private String tenancy = "conduvalle";

    public Set<ThirdPartyRoleDTO> findRoleByRoleName(String role){
        Set<ThirdPartyRoleDTO> roleDTOS = new HashSet<>();

        Set<TPRoleEntity> roleEntity = tenantService.executeInTenant(tenancy, () -> {
            return thirdPartyRoleRepository.findByRoleName(role);
        });

        roleEntity.forEach(rol -> roleDTOS.add(ThirdPartyRoleMapper.toDto(rol)));

        return roleDTOS;

    }
}
