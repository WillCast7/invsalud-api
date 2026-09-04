package com.aurealab.config.filter;

import com.aurealab.config.CustomUserDetails;
import com.aurealab.util.JwtUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = null;

            // 1. Buscar en encabezado Authorization nativo
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else if (authHeader != null && !authHeader.trim().isEmpty()) {
                token = authHeader.trim();
            }

            // 2. Buscar en encabezado 'token' alternativo
            if (token == null || token.trim().isEmpty()) {
                token = accessor.getFirstNativeHeader("token");
            }

            if (token != null && !token.trim().isEmpty()) {
                try {
                    DecodedJWT decodedJWT = jwtUtils.validateToken(token);
                    String userName = jwtUtils.extractUsername(decodedJWT);
                    Long userId = jwtUtils.extractUserId(decodedJWT);
                    String stringAuthorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();

                    Collection<? extends GrantedAuthority> authorities =
                            AuthorityUtils.commaSeparatedStringToAuthorityList(stringAuthorities != null ? stringAuthorities : "");

                    CustomUserDetails userDetails = new CustomUserDetails(userId, userName, "", authorities);
                    Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    accessor.setUser(auth);
                    if (accessor.getSessionAttributes() != null) {
                        accessor.getSessionAttributes().put("userId", userId);
                        accessor.getSessionAttributes().put("username", userName);
                    }
                    log.info("WebSocket autenticado correctamente para usuario: {} (ID: {})", userName, userId);
                } catch (Exception e) {
                    log.warn("Error autenticando token WebSocket: {}", e.getMessage());
                }
            } else {
                log.warn("Conexión WebSocket CONNECT recibida sin token de autorización");
            }
        }

        return message;
    }
}