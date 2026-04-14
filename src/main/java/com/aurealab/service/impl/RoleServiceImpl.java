package com.aurealab.service.impl;

import com.aurealab.dto.RoleDTO;
import com.aurealab.mapper.RoleMapper;
import com.aurealab.model.aurea.entity.RoleEntity;
import com.aurealab.model.aurea.repository.RoleRepository;
import com.aurealab.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository roleRepository;

    public Set<RoleDTO> getAllRoles(){
        Set<RoleEntity> roles = roleRepository.findAll();
        return roles.stream().map(RoleMapper::toDto).collect(Collectors.toSet());
    }
}
