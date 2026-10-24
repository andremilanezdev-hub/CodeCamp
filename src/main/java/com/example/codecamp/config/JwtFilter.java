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

// Valida o token Bearer antes de encaminhar uma requisição protegida ao controller.
@Component
public class JwtFilter extends OncePerRequestFilter {

    // Deve ser o mesmo segredo usado pelo LoginController para assinar o token.
    @Value("${spring.secret}")
    private String secret;

    // O emissor esperado impede aceitar tokens emitidos por outra aplicação.
    @Value("${spring.emissor}")
    private String emissor;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String caminho = request.getRequestURI();

        // Permite a verificação CORS do navegador sem exigir credenciais no preflight.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Login e documentação precisam continuar acessíveis sem token.
        if (caminho.equals("/login")
                || caminho.startsWith("/swagger-ui")
                || caminho.startsWith("/webjars")
                || caminho.startsWith("/swagger-resources")
                || caminho.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        // As demais rotas exigem um cabeçalho Bearer com token válido.
        String headerToken = request.getHeader("Authorization");
        if (headerToken == null || !headerToken.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token Bearer ausente ou inválido");
            return;
        }

        String token = headerToken.substring("Bearer ".length()).trim();
        try {
            // Confere assinatura, emissor e validade antes de confiar na identidade do subject.
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(emissor)
                    .build();
            String cpfAutenticado = verifier.verify(token).getSubject();

            if (cpfAutenticado == null || cpfAutenticado.isBlank()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token sem usuário associado");
                return;
            }

            // Disponibiliza a identidade verificada para as validações nos controllers e services.
            request.setAttribute("usuarioAutenticadoCpf", cpfAutenticado);
        } catch (JWTVerificationException exception) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expirado ou inválido");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
