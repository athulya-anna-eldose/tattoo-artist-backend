package com.example.my_new_project.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class AdminKeyFilter extends OncePerRequestFilter {

    @Value("${app.admin-key}")
    private String adminKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String method = request.getMethod();
        boolean isRead = method.equals("GET") || method.equals("HEAD")
                || method.equals("OPTIONS");

        if (!isRead && request.getRequestURI().startsWith("/api/")) {
            String sent = request.getHeader("X-Admin-Key");
            boolean ok = sent != null && MessageDigest.isEqual(
                    sent.getBytes(StandardCharsets.UTF_8),
                    adminKey.getBytes(StandardCharsets.UTF_8));

            if (!ok) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}