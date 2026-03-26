package org.example.domain.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.domain.security.JwtAuthentication;
import org.example.domain.service.security.JwtProvider;
import org.example.domain.service.security.JwtUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.UUID;

import static org.example.domain.Configuration.TYPE;

public class AuthFilter extends GenericFilterBean {
    private final JwtProvider jwtProvider;
    private final JwtUtil jwtUtil;

    public AuthFilter(JwtProvider jwtProvider, JwtUtil jwtUtil) {
        this.jwtProvider = jwtProvider;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI();

        if (path.equals("/auth/register") ||
                path.equals("/auth/login") ||
                path.equals("/auth/refresh/access") ||
                path.equals("/refresh/refresh")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(TYPE)) {
            response.sendError(401, "Требуется авторизация");
            return;
        }

        try {
            String token = authHeader.substring(TYPE.length()).trim();
            Claims claims = jwtProvider.getClaim(token);

            if (!jwtProvider.validateAccessToken(token)) {
                response.sendError(401, "Неверный логин или пароль");
                return;
            }

            UUID userId = UUID.fromString(claims.get("userId", String.class));
            request.setAttribute("userId", userId);

            JwtAuthentication authentication = jwtUtil.createJwtAuthentication(claims);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(401);
            response.getWriter().write(e.getMessage());

        }
    }
}