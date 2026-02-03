package com.example.micro_productos.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${api.key}")
    private String apiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        String path = request.getRequestURI();

        // 🔓 Excluir rutas públicas del filtro
        if (path.startsWith("/productos/public") ||
            path.startsWith("/productos/ping") ||
            path.startsWith("/inventario/ping") ||
            path.startsWith("/actuator/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("X-API-KEY");

        // Log de depuración
        System.out.println("Header recibido: " + header + " | Esperado: " + apiKey);

        if (header != null && header.equals(apiKey)) {
            filterChain.doFilter(request, response);
        } else {
            response.sendError(HttpStatus.UNAUTHORIZED.value(),
                    "Acceso denegado: API Key inválida o ausente");
        }
    }
}





            