package com.example.codecamp.controllers;


import com.example.codecamp.DTO.LoginRequest;
import com.example.codecamp.DTO.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {
    @PostMapping
    public ResponseEntity<LoginResponse> logar(@RequestBody LoginRequest loginRequest)  {

        if (loginRequest.getLogin().equals("string")&& loginRequest.getSenha().equals("string")) {

            LoginResponse LoginResponse = new LoginResponse();
            LoginResponse.setMensagem("Bem vindo! Ao Sistema de alunos");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
