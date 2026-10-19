package com.example.codecamp.controllers;

import com.example.codecamp.DTO.EnderecoResponse;
import com.example.codecamp.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enderecos")
public class EnderecoController {

    @Autowired
    private ViaCepService viaCepService;

    @GetMapping("/{cep}")
    public EnderecoResponse buscarEndereco(@PathVariable String cep) {
        return viaCepService.consultarCep(cep);
    }
}