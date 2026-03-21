package com.aurealab.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class CustomUserDetails extends User {
    private final Long id;
    private final String tenant;
    public CustomUserDetails(Long id, String username, String password, String tenant, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.tenant = tenant;
    }

    public Long getId() {
        return id;
    }
    public String getTenant() {
        return tenant;
    }
}
