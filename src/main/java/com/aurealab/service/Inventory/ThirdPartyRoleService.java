package com.aurealab.service.Inventory;

import com.aurealab.dto.CashRegister.ThirdPartyRoleDTO;

import java.util.List;
import java.util.Set;

public interface ThirdPartyRoleService {
    public Set<ThirdPartyRoleDTO> findRoleByRoleName(String role);
    public Set<ThirdPartyRoleDTO>  findAllEntitiesByIds(List<Long> ids);
    public Set<ThirdPartyRoleDTO> findAll();
}
