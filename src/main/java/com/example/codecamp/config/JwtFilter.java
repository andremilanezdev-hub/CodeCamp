package com.example.codecamp.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Intercepta as requisições e exige um JWT válido nas rotas protegidas.
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Value("${spring.secret}")
    private String secret;

    @Value("${spring.emissor}")
    private String emissor;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String caminho = request.getRequestURI();

        // Libera login, documentação e preflight CORS.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())
                || caminho.equals("/login")
                || caminho.startsWith("/swagger-ui")
                || caminho.startsWith("/webjars")
                || caminho.startsWith("/swagger-resources")
                || caminho.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token Bearer ausente ou inválido");
            return;
        }

        String token = authorization.substring("Bearer ".length()).trim();
        if (token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token Bearer ausente ou inválido");
            return;
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(emissor)
                    .build();

            String cpfAutenticado = verifier.verify(token).getSubject();
            if (cpfAutenticado == null || cpfAutenticado.isBlank()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token sem usuário associado");
                return;
            }

            // Usado pelos controllers que validam a identidade do instrutor.
            request.setAttribute("usuarioAutenticadoCpf", cpfAutenticado);
        } catch (JWTVerificationException exception) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expirado ou inválido");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
