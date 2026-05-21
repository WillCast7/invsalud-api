package com.aurealab.service.impl;

import com.aurealab.config.CustomUserDetails;
import com.aurealab.config.databases.multitenancy.TenantContext;
import com.aurealab.dto.*;
import com.aurealab.model.aurea.entity.PasswordResetTokenEntity;
import com.aurealab.model.aurea.entity.UserEntity;
import com.aurealab.model.aurea.repository.TokenRepository;
import com.aurealab.model.aurea.repository.UserRepository;
import com.aurealab.model.aurea.entity.MenuItemEntity;
import com.aurealab.model.aurea.entity.RoleEntity;
import com.aurealab.model.aurea.repository.RoleRepository;
import com.aurealab.service.EmailService;
import com.aurealab.util.JwtUtils;
import com.aurealab.util.constants;
import com.aurealab.util.exceptions.BaseException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class UserDetailServiceImpl {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    MenuServiceImpl menuServiceImpl;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    /**
     * Carga los detalles del usuario incluyendo roles, permisos y menú.
     */
    public UserDetails loadUserDetails(Long id, String roleName, RoleEntity role, String username) throws UsernameNotFoundException {

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        role.getPermissionList()
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRole())));

        log.info("Detalles del usuario cargados correctamente");
        return new CustomUserDetails(id, username, "password", authorityList);
    }

    /**
     * Proceso de inicio de sesión del usuario.
     */
    @Transactional
    public ResponseEntity<APIResponseDTO<AuthResponse>> loginUser(LoginRequest userLogin) {

        UserEntity userEntity = validateCredentials(userLogin.username());

        if (userEntity.getLoginAttempt() != null && userEntity.getLoginAttempt() >= 5) {
            throw new BaseException(constants.errors.userBlocked, constants.errors.userBlocked, HttpStatus.FORBIDDEN) {};
        }

        if (!passwordEncoder.matches(userLogin.password(), userEntity.getPassword())) {
            userEntity.setLoginAttempt(userEntity.getLoginAttempt() == null ? 1 : userEntity.getLoginAttempt() + 1);
            userRepository.save(userEntity);
            throw new BaseException(constants.errors.loginError, constants.descriptions.loginError) {};
        }

        try {

            userEntity.setLoginAttempt(0L);
            userRepository.save(userEntity);

            // Cargar detalles del usuario
            UserDetails userDetails = loadUserDetails(userEntity.getId(), userEntity.getRole().getRoleName(), userEntity.getRole(), userLogin.username());
            System.out.println(userEntity.getRole().getRoleName());
            Set<MenuItemEntity> optionalMenu = menuServiceImpl.getMenuByRoleName(userEntity.getRole().getRoleName());
            if (optionalMenu.isEmpty()){
                log.warn("No se encontró ningún menu asociado al validador: {}", userEntity.getRole());
                throw new UsernameNotFoundException(constants.errors.invalidMenu);
            }
            Set<MenuDTO> menuList = new HashSet();

            optionalMenu.stream().forEach(menuItemEntity -> menuList.add(setMenuEntityToDTO(menuItemEntity)));
            // 2. CRÍTICO: Pasamos 'userDetails' (que es CustomUserDetails) en lugar de solo el string
            String accessToken = jwtUtils.createToken(
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
            );


            AuthResponse authResponse = new AuthResponse(userLogin.username(), userEntity.getPerson().getNames()
                    , accessToken, menuList);


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
                .nameFather(menuParam.getNameFather())
                .orderMenu(menuParam.getOrderMenu())
                .build();
    }

    public ResponseEntity<APIResponseDTO<String>> forgotPassword(ForgotPasswordRequestDTO credentials) {
        UserEntity user = userRepository.findByUserNameAndDocumentNumber(credentials.username(), credentials.dniNumber()).orElseThrow(() ->
                new EntityNotFoundException(constants.messages.invalidCredentials)
        );

        PasswordResetTokenEntity resetToken = createTokenForUser(user.getId());
        emailService.sendAccountRecoveryEmail(user, resetToken.getToken());

        return ResponseEntity.ok(APIResponseDTO.success(constants.success.forgotPassSuccess, constants.success.forgotPassSuccess));
    }

    public PasswordResetTokenEntity createTokenForUser(Long userId) {
        PasswordResetTokenEntity resetToken = new PasswordResetTokenEntity();

        // Generamos un string único e impredecible (UUID v4)
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setUserId(userId);

        // Configuramos los 15 minutos de vida útil
        resetToken.setExpireDate(LocalDateTime.now().plusMinutes(15));
        resetToken.setUsed(false);

        return tokenRepository.save(resetToken);
    }

    public ResponseEntity<APIResponseDTO<String>> resetPassword(String token, String newPassword){
        PasswordResetTokenEntity resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BaseException(constants.errors.tokenValidationError, "Token inválido o no encontrado", HttpStatus.BAD_REQUEST) {});

        if (resetToken.isUsed()) {
            throw new BaseException("Este enlace ya ha sido utilizado.", "Este enlace ya ha sido utilizado.", HttpStatus.BAD_REQUEST) {};
        }

        if (resetToken.getExpireDate().isBefore(LocalDateTime.now())) {
            throw new BaseException("TOKEN_EXPIRED", "El enlace ha expirado (límite de 15 minutos).", HttpStatus.BAD_REQUEST) {};
        }

        UserEntity user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new BaseException(constants.errors.invalidUser, "Usuario no encontrado", HttpStatus.NOT_FOUND) {});

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return ResponseEntity.ok(APIResponseDTO.success("Contraseña modificada con éxito.", "Contraseña modificada con éxito."));
    }

    public ResponseEntity<APIResponseDTO<String>> confirmToken(String token) {
        PasswordResetTokenEntity resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BaseException( "Token inválido o no encontrado", "TOKEN_NOT_FOUND", HttpStatus.BAD_REQUEST) {});

        if (resetToken.isUsed()) {
            throw new BaseException("Este enlace ya ha sido utilizado.", "TOKEN_ALREADY_USE.", HttpStatus.BAD_REQUEST) {};
        }

        if (resetToken.getExpireDate().isBefore(LocalDateTime.now())) {
            throw new BaseException("El enlace ha expirado (límite de 15 minutos)", "TOKEN_EXPIRED", HttpStatus.BAD_REQUEST) {};
        }

        return ResponseEntity.ok(APIResponseDTO.success("Token válido", "Token válido"));
    }
}

