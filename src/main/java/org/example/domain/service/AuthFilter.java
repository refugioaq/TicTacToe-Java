package org.example.domain.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

public class AuthFilter extends GenericFilterBean {
    private final UserService userService;

    public AuthFilter(UserService userService) {
        this.userService = userService;
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

        if(path.contains("auth/register") || path.contains("auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Basic ")) {
            response.sendError(401, "Требуется авторизация");
            return;
        }

        String base64Credentials = authHeader.substring("Basic ".length());

        try {
            UUID id = userService.authorize(base64Credentials);
            request.setAttribute("userId", id);
            chain.doFilter(request, response);
        } catch (Exception e) {
            System.err.println("Auth error: " + e.getMessage());
            response.sendError(401, "Неверный логин или пароль");
        }
    }
}
