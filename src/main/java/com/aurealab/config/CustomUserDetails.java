package com.aurealab.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class CustomUserDetails extends User {
    private final Long id; // Aquí guardaremos el ID de la base de datos
    private final Long roleId; // Rol del usuario

    public CustomUserDetails(Long id, Long roleId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.roleId = roleId;
    }

    public CustomUserDetails(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this(id, null, username, password, authorities);
    }

    public Long getId() {
        return id;
    }

    public Long getRoleId() {
        return roleId;
    }
}
