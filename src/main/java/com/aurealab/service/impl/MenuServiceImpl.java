package com.aurealab.service.impl;

import com.aurealab.model.aurea.entity.MenuItemEntity;
import com.aurealab.model.aurea.repository.MenuRepository;
import com.aurealab.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    MenuRepository menuRepository;

    public Set<MenuItemEntity> getMenuByRoleName(String role){
        return menuRepository.findByRoleName(role);
    }

}
