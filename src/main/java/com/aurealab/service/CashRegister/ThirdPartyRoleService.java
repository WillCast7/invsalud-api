package com.aurealab.service.CashRegister;

import com.aurealab.dto.CashRegister.ThirdPartyRoleDTO;
import com.aurealab.model.cashRegister.entity.TPRoleEntity;

import java.util.List;
import java.util.Set;

public interface ThirdPartyRoleService {
    public Set<ThirdPartyRoleDTO> findRoleByRoleName(String role);
    public Set<ThirdPartyRoleDTO>  findAllEntitiesByIds(List<Long> ids);
    public Set<ThirdPartyRoleDTO> findAll();
}
