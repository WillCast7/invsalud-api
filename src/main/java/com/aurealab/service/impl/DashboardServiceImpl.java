package com.aurealab.service.impl;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.MenuDTO;
import com.aurealab.dto.response.UserTableResponseDTO;
import com.aurealab.mapper.MenuMapper;
import com.aurealab.model.aurea.entity.MenuItemEntity;
import com.aurealab.model.aurea.entity.UserEntity;
import com.aurealab.service.DashboardService;
import com.aurealab.service.MenuService;
import com.aurealab.service.UserService;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    MenuService menuService;


    public ResponseEntity<APIResponseDTO<Set<MenuDTO>>> getUsersMenu() {
        UserEntity user = userService.getUserEntityById(jwtUtils.getCurrentUserId());

        Set<MenuDTO> menu = new HashSet<>();
        menuService.getMenuByRoleName(user.getRole().getRoleName()).forEach(menuItemEntity ->
                menu.add(MenuMapper.toDto(menuItemEntity)));


        return ResponseEntity.ok(
                APIResponseDTO.success(
                    menu,
                    constants.success.findedSuccess
                )
        );
    }
}
