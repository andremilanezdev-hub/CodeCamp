package com.example.codecamp.config;
<<<<<<< HEAD
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
=======

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
>>>>>>> origin/main
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

<<<<<<< HEAD
// Intercepta cada requisição HTTP para exigir um token nas rotas protegidas.
@Component
public class JwtFilter extends OncePerRequestFilter {

    // Valor resolvido de spring.secret; a configuração aponta para a variável de ambiente JWT_SECRET.
    @Value("${spring.secret}")
    private String secret;

    // Valor de spring.emissor; deve corresponder ao emissor gravado pelo LoginController no JWT.
    @Value("${spring.emissor}")
    private String emissor;




    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // O caminho e o método vêm da requisição recebida pelo servidor, não do banco de dados.
        String caminho = request.getRequestURI();

        // Login é necessário para obter um token; Swagger e preflight CORS são liberados sem autenticação.
        if(caminho.equals("/login") ||
                caminho.startsWith("/swagger-ui") ||
                caminho.startsWith("/webjars") ||
                caminho.startsWith("/swagger-resources") ||
                caminho.startsWith("/v3/api-docs")||
                request.getMethod().startsWith("OPTIONS")
        ){
            filterChain.doFilter(request,response);
            return;
        }

        // O token das outras rotas é recebido no cabeçalho HTTP Authorization.
        String headerToken = request.getHeader("Authorization");

        if (headerToken!= null && headerToken.startsWith("Bearer")){

            // Remove o prefixo Bearer para entregar somente o JWT ao verificador.
            String token = headerToken.replace("Bearer ","");


            // Recria o algoritmo de assinatura com o mesmo segredo usado no login.
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // Exige que o emissor do token corresponda à propriedade spring.emissor.
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(emissor)
                    .build();

            // Verifica assinatura, emissor e validade temporal; em seguida lê o subject (CPF).
            // JWTVerificationException não é tratada nesta implementação e pode encerrar a requisição com erro 500.
            var resultado = verifier.verify(token);

            // Apenas imprime o subject no console; não o associa à requisição nem consulta o banco.
            System.out.println(resultado.getSubject());


        }else {
            // Cabeçalho ausente ou fora do formato esperado: responde 401 e interrompe o fluxo.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token invalido!");
            return;
        }

        // Encaminha a requisição para o próximo filtro/controller após a validação.
        filterChain.doFilter(request,response);

=======
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
>>>>>>> origin/main
    }
}
