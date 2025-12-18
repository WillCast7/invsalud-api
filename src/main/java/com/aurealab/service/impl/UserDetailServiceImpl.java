package com.aurealab.service.impl;

import com.aurealab.dto.*;
import com.aurealab.model.aurea.entity.UserEntity;
import com.aurealab.model.aurea.repository.UserRepository;
import com.aurealab.model.aurea.entity.MenuItemEntity;
import com.aurealab.model.aurea.entity.RoleEntity;
import com.aurealab.model.aurea.repository.RoleRepository;
import com.aurealab.util.DecryptPass;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import com.aurealab.util.exceptions.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class UserDetailServiceImpl {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    MenuServiceImpl menuServiceImpl;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Carga los detalles del usuario incluyendo roles, permisos y menú.
     */
    public UserDetails loadUserDetails(String roleName, RoleEntity role, String username) throws UsernameNotFoundException {
        log.info("Cargando detalles del usuario con rol: {}", roleName);

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        role.getPerrmissionList()
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRole())));

        log.info("Detalles del usuario cargados correctamente");
        return new User(username, "password", authorityList);
    }

    /**
     * Proceso de inicio de sesión del usuario.
     */
    public ResponseEntity<APIResponseDTO<AuthResponse>> loginUser(LoginRequest userLogin) {

        String passwordEncode = passwordEncoder.encode(userLogin.password());

        UserEntity userEntity = validateCredentials(userLogin.username());
        System.out.println("pass ");
        System.out.println(passwordEncode);
        if (!passwordEncoder.matches(userLogin.password(), userEntity.getPassword())) {
            throw new BaseException(constants.errors.loginError, constants.descriptions.loginError) {};
        }

        try {

            // Cargar detalles del usuario
            UserDetails userDetails = loadUserDetails(userEntity.getRole().getRoleName(), userEntity.getRole(), userLogin.username());
            System.out.println(userEntity.getRole().getRoleName());
            Set<MenuItemEntity> optionalMenu = menuServiceImpl.getMenuByRoleName(userEntity.getRole().getRoleName());
            if (optionalMenu.isEmpty()){
                log.warn("No se encontró ningún menu asociado al validador: {}", userEntity.getRole());
                throw new UsernameNotFoundException(constants.errors.invalidMenu);
            }
            Set<MenuDTO> menuList = new HashSet();

            optionalMenu.stream().forEach(menuItemEntity -> menuList.add(setMenuEntityToDTO(menuItemEntity)));
            String accessToken = jwtUtils.createToken(
                new UsernamePasswordAuthenticationToken(userLogin.username(), userLogin.password(), userDetails.getAuthorities())
            );


            AuthResponse authResponse = new AuthResponse(userLogin.username(), accessToken, menuList);


            return ResponseEntity.ok(APIResponseDTO.success(authResponse, constants.success.loginSuccess)); // Enviar cookie en la respuesta

        } catch (Exception e) {
            log.error("error: {}" + e);
            throw new ConcurrentModificationException(e);
        }
    }

    /**
     * Valida las credenciales del usuario en la base de datos.
     */
    public UserEntity validateCredentials(String username) {
            System.out.println("username: " + username);
            return userRepository.findByUserNameOrEmail(username, username)
                    .orElseThrow(() -> new BaseException(
                            constants.errors.invalidUser,
                            constants.descriptions.loginError,
                            HttpStatus.UNAUTHORIZED) {}
                    );
    }

    public MenuDTO setMenuEntityToDTO(MenuItemEntity menuParam) {
        if (menuParam == null) {
            return null;
        }

        return MenuDTO.builder()
                .id(menuParam.getId())
                .name(menuParam.getName())
                .route(menuParam.getRoute())
                .icon(menuParam.getIcon())
                .father(menuParam.getFather())
                .orderMenu(menuParam.getOrderMenu())
                .build();
    }
}
