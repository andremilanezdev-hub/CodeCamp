package com.example.codecamp.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.codecamp.DTO.LoginRequest;
import com.example.codecamp.DTO.LoginResponse;
import com.example.codecamp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Lê o segredo de assinatura da configuração externa para não fixá-lo no código.
    @Value("${spring.secret}")
    private String secret;

    // Identifica a aplicação que emitiu o token.
    @Value("${spring.emissor}")
    private String emissor;

    // Define por quantos minutos o token permanece válido.
    @Value("${spring.tempo}")
    private Long tempo;

    @PostMapping
    public ResponseEntity<LoginResponse> logar(@RequestBody LoginRequest loginRequest)  {

//        Usuario usuarioBanco = usuarioRepository.findAll().stream().filter(usuario -> usuario.getCpf())
        // Só emite token depois que o CPF e a senha forem validados no banco.
        if (usuarioRepository.existsUsuarioByCpfAndSenha(loginRequest.getLogin(),loginRequest.getSenha())) {

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setMensagem("Bem vindo! Ao Sistema de BotCamp");

            // HMAC256 assina o token para que o filtro possa detectar alterações no conteúdo.
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // Emissor identifica a API; subject identifica o usuário pelo CPF.
            String token = JWT.create()
                    .withIssuer(emissor)
                    .withSubject(loginRequest.getLogin())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algorithm);
            loginResponse.setToken(token);
            return ResponseEntity.ok(loginResponse);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Calcula a expiração como instante UTC, evitando depender do fuso horário da máquina.
    private Instant gerarDataExpiracao(){
        return Instant.now().plus(tempo, ChronoUnit.MINUTES);
    }

}
