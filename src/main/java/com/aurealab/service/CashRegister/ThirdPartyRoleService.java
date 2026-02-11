package com.aurealab.service.CashRegister;

import com.aurealab.dto.CashRegister.ThirdPartyRoleDTO;

import java.util.Set;

public interface ThirdPartyRoleService {
    public Set<ThirdPartyRoleDTO> findRoleByRoleName(String role);
}
