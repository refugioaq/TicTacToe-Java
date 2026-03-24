package org.example.domain.service.security;

import io.jsonwebtoken.Claims;
import org.example.domain.model.Role;
import org.example.domain.security.JwtAuthentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

public class JwtUtil {

    public JwtAuthentication createJwtAuthentication(Claims claims) {
        UUID userId = UUID.fromString(claims.get("userId", String.class));;
        Object roles = claims.get("role");

        Collection<? extends GrantedAuthority> list = Collections.emptyList();

        if (roles instanceof Collection<?> collection) {
            list = collection
                    .stream()
                    .map(Object::toString)
                    .map(Role::valueOf)
                    .collect(Collectors.toSet());
        }
        return new JwtAuthentication(userId, list, true);
    }
}
