package com.aurealab.service;

import com.aurealab.model.aurea.entity.MenuItemEntity;

import java.util.Set;

public interface MenuService {
    public Set<MenuItemEntity> getMenuByRoleName(String role);
}
