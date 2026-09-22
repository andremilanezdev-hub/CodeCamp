package com.example.codecamp.controllers;

import com.example.codecamp.DTO.LoginRequest;
import com.example.codecamp.DTO.LoginResponse;
import com.example.codecamp.entities.Usuario;
import com.example.codecamp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<LoginResponse> logar(@RequestBody LoginRequest loginRequest)  {

//        Usuario usuarioBanco = usuarioRepository.findAll().stream().filter(usuario -> usuario.getCpf())

        if (usuarioRepository.existsUsuarioByCpfAndSenha(loginRequest.getLogin(),loginRequest.getSenha())) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setMensagem("Bem vindo! Ao Sistema de BotCamp");
            return ResponseEntity.ok(loginResponse);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
