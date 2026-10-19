package com.example.codecamp.service;

import com.example.codecamp.DTO.EnderecoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ViaCepService {

    public EnderecoResponse consultarCep(String cep) {
        // Remove qualquer caractere que não seja número (ex: traços ou pontos)
        String cepLimpo = cep.replaceAll("\\D", "");

        String url = "https://viacep.com.br/ws/" + cepLimpo + "/json/";

        RestTemplate restTemplate = new RestTemplate();

        // Faz a requisição GET e converte o JSON automaticamente para o nosso Record
        return restTemplate.getForObject(url, EnderecoResponse.class);
    }
}