package com.example.codecamp.DTO;

public record EnderecoResponse(
            String cep,
            String logradouro,
            String complemento,
            String bairro,
            String localidade,
            String uf,
            String ddd
)
{}
