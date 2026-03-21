package com.aurealab.util;


import com.aurealab.config.CustomUserDetails;
import com.aurealab.util.exceptions.TokenException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtils {
    //LionsFamily

    @Value("${security.jwt.key.private}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    /**
     * Create a JWT TOKEN
     *
     * @param authentication
     * @return a token
     */
    public String createToken(Authentication authentication) {
        try {
            // Algoritmo de firma
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

            // Obtén el username e id desde el principal
            String username;
            Long userId = null;
            String tenant = null;

            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                username = userDetails.getUsername();
                userId = userDetails.getId(); // Extraemos el ID
                tenant = userDetails.getTenant();
            } else {
                username = authentication.getPrincipal().toString();
            }

            // Obtén las autoridades
            String authorities = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
            System.out.println("authorities");
            System.out.println(authorities);
            // Crear el token
            String jwtToken = JWT.create()
                    .withIssuer(this.userGenerator)
                    .withSubject(username)
                    .withClaim("userId", userId) // <--- AGREGAMOS EL ID AL JWT
                    .withClaim("tenant", tenant)
                    .withClaim("authorities", authorities) // Añade las autoridades
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 10800000)) // 3 horas de expiración
                    .withJWTId(UUID.randomUUID().toString())
                    .withNotBefore(new Date(System.currentTimeMillis()))
                    .sign(algorithm);

            return jwtToken;

        } catch (JWTCreationException exception) {
            throw new TokenException("Error al generar el token JWT.", exception);
        }
    }


    /**
     * Token Validation
     * @param token
     * @return Token decoded
     */
    public DecodedJWT validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();

            return verifier.verify(token);
        } catch (JWTVerificationException exception){
            log.error("Error decodificando el token: ", exception);
            throw new TokenException("Token inválido o expirado", exception);
        }
    }

    public String extractUsername(DecodedJWT decodedJWT){
        return decodedJWT.getSubject();
    }

    public String extractTenant(DecodedJWT decodedJWT){
        return decodedJWT.getClaim("tenant").asString();
    }

    public Long extractUserId(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim("userId").asLong();
    }

    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName){
        return decodedJWT.getClaim(claimName);
    }

    public Map<String, Claim> getAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails user) {
            return user.getId();
        }
        return null;
    }

    public String getCurrentTenant(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails user) {
            if (user.getTenant() == null) {
                throw new TokenException("El usuario no tiene un Tenant asignado en el contexto de seguridad.", null);
            }
            return user.getTenant();
        }
        return null;
    }
}
