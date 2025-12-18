package com.aurealab.config.databases.multitenancy;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Priorizar JWT -> header -> fallback
            String tenant = null;

            // 1. ejemplo: extraer desde header
            tenant = request.getHeader("X-TENANT");

            // 2. si usas JWT: extrae tenant del token (implementa tu método)
            // if (tenant == null) { tenant = JwtUtils.extractTenantFromRequest(request); }

            if (tenant == null || tenant.isEmpty()) {
                // opcional: tenant por defecto o lanzar excepción
                // tenant = "public"; // o lanzar 400
                filterChain.doFilter(request, response);
                return;
            }

            TenantContext.setCurrentTenant(tenant);
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}